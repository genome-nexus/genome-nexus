# Plan: Serve Cancer Hotspots v3 data in genome-nexus

## Background / data confirmed

- `genome-nexus-importer` local `master` has been fast-forwarded to `upstream/master`
  (`256da20` → `7645153`), which pulls in PR #124 "Add Cancer Hotspots v3 support
  (opt-in)". `data/grch37_ensembl111/export/hotspots_v2_and_3d.txt` now has 7074 rows
  (6910 tagged `v2`, 164 tagged `v3`) instead of 6910, and gained a new 20th column,
  `version`, with values `v2` / `v3`.
- The importer's mongoimport (`genome-nexus-importer/scripts/import_mongo.sh:115`)
  loads this TSV with `--type tsv --headerline`, so the header row drives the Mongo
  field names automatically. **No importer-side script changes are needed** — once
  genome-nexus's own data submodule/checkout is bumped to this importer commit, the
  `hotspot.mutation` collection will already contain a `version` field on every
  document after the next import.
- v2 and v3 are **mutually exclusive** per `(hugo_symbol, residue)` — the importer
  only tags a row `v3` if that `hugo_symbol`+`residue` pair didn't already exist in
  v2. So "return both versions" for a given query is already what happens today by
  default: nothing in the existing query/filter code path filters on `version`, and
  none of it needs to change to keep returning both. The two additions are (1)
  exposing the `version` value in the JSON that's already being returned, and (2)
  adding an opt-in filter in the dedicated hotspots controller.
- The importer's mongoimport upsert key (`import_mongo.sh:115`,
  `--upsertFields hugo_symbol,residue,type,tumor_count`) does **not** include
  `version`. This is safe given the mutual-exclusivity invariant above (a v2 row and
  a v3 row can never share the same `hugo_symbol`+`residue`, so they can't collide
  on this upsert key), but it's worth a one-line callout in the importer PR/README
  if not already documented, since it's a latent footgun if that invariant ever
  changes. No code change planned for it here — out of scope, importer repo.

## Current architecture (genome-nexus, `add-cancerhotspots-v3` worktree)

Two independent consumers of hotspot data today:

1. **`AnnotationController`** (`web/.../AnnotationController.java`) — the general
   VEP annotation endpoints (`/annotation`, `/annotation/{variant}`,
   `/annotation/genomic`, `/annotation/dbsnp/...`). All of them accept a
   `fields` query param typed `List<AnnotationField>`
   (`model/.../AnnotationField.java`). When `fields` contains `HOTSPOTS`,
   `VariantAnnotationService.initPostEnrichmentService()` (line ~349) attaches a
   `HotspotAnnotationEnricher`, which calls
   `CancerHotspotService.getHotspots(transcript, annotation)` for each transcript
   consequence and sets the result on `VariantAnnotation.hotspotAnnotation`
   (a `HotspotAnnotation` wrapping `List<List<Hotspot>>`).

2. **`CancerHotspotsController`** (`web/.../CancerHotspotsController.java`) — a
   standalone REST API (`/cancer_hotspots/hgvs/...`, `/genomic/...`,
   `/transcript/...`, `/proteinLocations`) backed by the same
   `CancerHotspotService` → `CancerHotspotServiceImpl` → `HotspotRepository`
   (Spring Data Mongo repo, `@Document(collection = "hotspot.mutation")`).

Both paths bottom out in the same `Hotspot` model
(`model/.../Hotspot.java`) and the same `HotspotFilter` component
(`component/.../HotspotFilter.java`) for position/mutation-type matching — there is
one code path to change, not two independent ones.

`Hotspot` currently maps these Mongo fields: `hugo_symbol`, `transcript_id`,
`transcript_id_version`, `residue`, `tumor_count`, `type`, `missense_count`,
`trunc_count`, `inframe_count`, `splice_count`. It has **no `version` field today**.
Serialization to JSON goes through `HotspotMixin` (`web/.../mixin/HotspotMixin.java`),
a Jackson mixin with `@JsonInclude(NON_NULL)` that whitelists which `Hotspot`
getters get serialized.

## Confirmed requirements (from Q&A)

1. `AnnotationController` endpoints: no new query param. When `hotspots` is
   requested via `fields`, the response continues to include both v2 and v3
   hotspots (this already happens automatically — see above) and each hotspot
   object gains a `version` field.
2. `CancerHotspotsController`: add a `version` query param, accepted on **every**
   endpoint (`hgvs` GET/POST, `genomic` GET/POST, `transcript` GET/POST,
   `proteinLocations` POST). Accepted values: `v2`, `v3` only (no `all`).
   - `version=v2` → only hotspots tagged `version: "v2"`.
   - `version=v3` → both `v2`-tagged and `v3`-tagged hotspots (v3 is treated as the
     cumulative/superset dataset, not v3-only).
   - Param omitted (default) → same as `version=v3`, i.e. both versions returned.
3. JSON field name in the hotspot response: `version` (matches the source column
   name, exposed as `"version": "v2"` / `"version": "v3"`).
4. Verification for this change is via updated JUnit fixtures/integration tests
   (no live local Mongo import required as part of this task).
5. **Backward compatibility**: genome-nexus must not break against an older
   `hotspot.mutation` collection that was imported before the `version` column
   (and, separately, before the `transcript_id_version` column) existed — i.e.
   documents where those fields are simply absent, not just empty-string.

## Backward compatibility with older/pre-v3 databases

Some deployments won't have re-run the importer against the new
`hotspots_v2_and_3d.txt` yet, so their `hotspot.mutation` documents may lack the
`version` field entirely (and, for older-still deployments, the
`transcript_id_version` field too — see `9b687c4` "Add support for transcript
subversions" in this repo's own history). Spring Data MongoDB doesn't error on a
missing field — it just leaves the corresponding Java field `null` — so nothing
crashes by default. The two things that actually need explicit handling:

1. **`transcript_id_version` — already safe, verified, no change needed.**
   Grepped every usage of `getTranscriptIdVersion()`/`transcriptIdVersion` across
   the codebase: it's purely a passthrough/display field on `Hotspot`,
   `AggregatedHotspots`, `TranscriptConsequence`, `TranscriptConsequenceSummary`,
   and `EnsemblTranscript` — nowhere is it parsed, concatenated, or used as a
   match/filter key (hotspot-to-transcript matching is done via `transcript_id`
   only, see `c03feda` "Make sure hotspots match the transcript id"). A `null`
   value flows straight through to `HotspotMixin`, which is `@JsonInclude(NON_NULL)`,
   so it's simply omitted from the JSON response instead of appearing as
   `"transcriptIdVersion": null`. No code change required; will add one legacy
   test fixture (see step 6) to lock this in as a regression test rather than an
   assumption.

2. **`version` — needs explicit null-safe handling, since this PR adds real
   filtering logic that a missing field could silently break.** Two ways this
   could go wrong if not handled deliberately:
   - If `version=v2` filtering were implemented as a Mongo query
     `{ version: "v2" }`, that query would match **zero** documents on a
     pre-migration database, since none of the legacy documents have a `version`
     field at all — an old, fully-legacy deployment would see `version=v2`
     silently return an empty list instead of "all its data" (which, prior to v3
     existing, *was* implicitly v2-equivalent data).
   - Any Java-side equality check like `"v2".equals(hotspot.getVersion())` has the
     same problem in reverse-safe direction (never NPEs, since the literal is on
     the left) but still silently excludes legacy documents from a `v2` filter.
   - The **default**/`v3` path is unaffected either way, since it applies no
     filter at all — legacy documents come back regardless, exactly as they do
     today. Only the `v2` filter needs care.
   - **Decision**: treat a missing/`null` `version` field as implicitly `"v2"`
     wherever version filtering happens (see step 4 below for exactly where).
     This makes `version=v2` against a fully-legacy database return everything
     (matching pre-this-change behavior), and against a mixed/fully-migrated
     database return exactly the `v2`-tagged subset, which is the correct
     semantic in both cases.

## Memory / performance impact (checked against large annotation jobs)

Verified this change introduces no meaningful memory risk, and specifically does
not touch the code path used by large bulk-annotation jobs:

- The `POST /annotation` bulk path (large MAF/VCF-style jobs) always uses the
  `V3`/unfiltered hotspots behavior per requirement #1 — no new filtering or
  allocation is added to `HotspotAnnotationEnricher`/`getHotspots(transcript,
  annotation)`. The only addition on that path is one extra `String` field
  (`version`) on objects that were already being built.
- The full `hotspots_v2_and_3d.txt` reference dataset is only 1.4MB / 7075 rows
  (up from 6910 pre-v3, a 2.4% increase) — far too small to threaten server memory
  regardless of job size, since a job's variant count doesn't change how much
  reference data exists, only how many times the same small dataset is looked up.
- `HotspotRepository.findByTranscriptId` is `@Cacheable("hotspotsByTranscriptId")`
  (`persistence/.../HotspotRepository.java:46`). Checked `GenomeNexusAnnotation.java`
  (`@EnableCaching`, no Caffeine/EhCache on the classpath) — this is Spring Boot's
  default unbounded, non-evicting `ConcurrentMapCacheManager`. This is
  **pre-existing behavior, unrelated to this change**, and it's self-limiting: the
  cache can hold at most one entry per distinct transcript ID present in the ~7k-row
  hotspot collection, regardless of how many variants a job processes.
- Confirmed `CancerHotspotService.getHotspots()` (the no-arg `findAll()`, which
  would pull the entire collection into memory at once) is unused anywhere in
  production code.
- The only new per-request work this feature adds — the in-memory `version=v2`
  null-safe filter (step 4) — runs only on the standalone `CancerHotspotsController`
  endpoints, operates on a handful of hotspots per transcript, and is garbage
  collected at the end of that single request. It is not on the bulk-annotation
  code path.

## Implementation plan

### 1. Data model — `model/src/main/java/org/cbioportal/genome_nexus/model/Hotspot.java`
- Add `@Field(value="version") private String version;` plus getter/setter.
- Consider whether `hashCode()`/`equals()` (currently
  `hugoSymbol + residue + type + tumorCount`) should incorporate `version`. Since
  v2/v3 are mutually exclusive per `(hugo_symbol, residue)`, this is very unlikely
  to matter in practice, but it's a one-line safety addition worth doing since the
  existing `LinkedHashSet<Hotspot>` dedup logic in `CancerHotspotServiceImpl`
  relies on it.

### 2. JSON exposure — `web/src/main/java/org/cbioportal/genome_nexus/web/mixin/HotspotMixin.java`
- Add `@ApiModelProperty(value = "Hotspot version (v2 or v3)") private String version;`
  so it's included (mixin already uses `@JsonInclude(NON_NULL)`, matching the
  existing pattern for other fields).
- This one change covers **both** consumers (`AnnotationController`'s
  `hotspotAnnotation.annotation[][]` and `CancerHotspotsController`'s `Hotspot`/
  `AggregatedHotspots` responses), since both serialize the same `Hotspot` class
  through the same mixin.

### 3. New `HotspotVersion` enum + Spring converter
- New file `model/src/main/java/org/cbioportal/genome_nexus/model/HotspotVersion.java`,
  mirroring `AnnotationField.java`'s style: enum constants `V2("v2")`, `V3("v3")`
  only (no `ALL`), with `fromString()` and `@JsonValue`.
- New file
  `web/src/main/java/org/cbioportal/genome_nexus/web/converters/HotspotVersionEnumConverter.java`,
  mirroring the existing `AnnotationFieldEnumConverter` so Spring can bind the
  `version` request param to the enum (case-insensitive), and register it in
  `web/src/main/java/org/cbioportal/genome_nexus/web/config/AppWebMvcConfigurer.java`
  (line 16 currently registers `AnnotationFieldEnumConverter`) alongside it.
  A converter is needed here (rather than relying on Spring's default enum
  binding, as `web/.../param/TranscriptSummaryProjection.java` does for
  `?projection=ALL|CANONICAL` in `AnnotationSummaryController`) specifically
  because the agreed wire values (`v2`, `v3`) are lowercase and don't match Java
  enum constant naming conventions (`V2`, `V3`) — same reasoning as why
  `AnnotationField` needed its own converter for snake_case values like
  `my_variant_info`.
- Semantics live in the enum/service layer, not just naming: `V2` means
  "version == v2 only"; `V3` means "version == v2 or v3" (v3 is the cumulative
  dataset). This asymmetry should be documented directly on the enum constants
  and/or the filtering method, since it's not obvious from the name alone that
  `V3` doesn't mean "v3-only."

### 4. Repository / service filtering — version-aware, null-safe

Given the backward-compatibility decision above (treat missing `version` as
implicit `v2`), filtering **in memory after the existing unfiltered fetch** is
simpler and safer than pushing a `{version: "v2"}` Mongo query down to the
repository — a Mongo query would need extra `$exists: false` plumbing to get the
same null-safe behavior, and in-memory filtering also matches the codebase's
existing style (`HotspotFilter` already does all its position/type filtering
in-memory after a per-transcript Mongo fetch, not via query predicates).

- `HotspotRepository` (`persistence/.../HotspotRepository.java`): **no changes**.
  Keep using `findByTranscriptId` for every case, `v2` included.
- New null-safe predicate, e.g. a static helper on `HotspotVersion` or a small
  method on `HotspotFilter`/`CancerHotspotServiceImpl`:
  ```java
  // true if hotspot should be included when the caller asked for V2
  boolean isV2(Hotspot h) {
      return h.getVersion() == null || "v2".equals(h.getVersion());
  }
  ```
  `V3`/default never calls this — it just returns the unfiltered list.
- `CancerHotspotService` interface + `CancerHotspotServiceImpl`: every public
  method that currently has no version concept
  (`getHotspots(transcriptId)`, `getHotspots(transcript, annotation)`,
  `getHotspotAnnotationsByVariant(s)`, `getHotspotAnnotationsByGenomicLocation(s)`,
  `getHotspotsByTranscriptIds`, `getHotspotAnnotationsByProteinLocations`) needs an
  overload/parameter that accepts a `HotspotVersion` and, only when it's `V2`,
  applies the null-safe filter above to the result of the existing unfiltered
  fetch/logic. `V3` paths are functionally unchanged.
- **Important constraint**: `CancerHotspotService.getHotspots(TranscriptConsequence, VariantAnnotation)`
  (used by `HotspotAnnotationEnricher`, i.e. the `AnnotationController` path) must
  keep its existing no-version-filter behavior/signature (or explicitly default to
  `V3`/both) so requirement #1 (`hotspots` field always returns both versions) is
  untouched.

### 5. `CancerHotspotsController` — add `version` param to all 7 endpoints
- Add `@RequestParam(required = false, defaultValue = "v3") HotspotVersion version`
  to:
  `fetchHotspotAnnotationByHgvsGET`, `fetchHotspotAnnotationByHgvsPOST`,
  `fetchHotspotAnnotationByGenomicLocationGET`,
  `fetchHotspotAnnotationByGenomicLocationPOST`,
  `fetchHotspotAnnotationByTranscriptIdGET`,
  `fetchHotspotAnnotationByTranscriptIdPOST`,
  `fetchHotspotAnnotationByProteinLocationsPOST`.
- Update each `@ApiParam`/Swagger doc string to describe the new param, e.g.
  `"v2 (v2 hotspots only) or v3 (v2 + v3 hotspots, default)"`.
- Pass `version` through to the corresponding `CancerHotspotService` call added in
  step 4.

### 6. Test fixtures and tests

Went through the existing hotspot-related test suite file by file (there is no
single "hotspots test module" — coverage is split across a mock-based service
test, an HTTP integration test, and shared fixture/mixin helpers) to make sure
every place touched by this feature, or that could silently break because of it,
gets updated:

**a) `web/src/test/resources/hotspot/hotspots_integration_test.json`**
(the fixture `CancerHotspotsIntegrationTest` loads into the embedded Mongo via
`HotspotRepository.saveAll(...)`, per `JsonToObjectMapper.readHotspots(...)`
using the test-only `web/.../web/mock/HotspotMixin.java`):
- Add `"version": "v2"` to all existing entries (they're all legacy/v2-era data).
- Add at least one new entry with `"version": "v3"` (pick a gene/residue/transcript
  not already in the fixture, e.g. a synthetic one, to avoid colliding with
  existing test assertions keyed on gene/residue).
- Add at least one entry that **omits `version` entirely** and, separately, one
  that **omits `transcript_id_version` entirely**, to simulate a pre-migration
  Mongo document (see Backward Compatibility section) — don't just set them to
  `null`/`""` in the JSON, actually leave the key out, since that's what
  distinguishes "field never populated" from "field populated as empty."

**b) `web/src/test/java/org/cbioportal/genome_nexus/web/mock/HotspotMixin.java`**
(test-only mock mixin used to *read* the fixture above): add
`@JsonProperty(value="version") private String version;` alongside the existing
`transcriptIdVersion` field (line 15-16) so the fixture's new `version` key
actually deserializes into the `Hotspot` objects the test inserts into Mongo —
without this, part (a)'s new fixture rows would silently load with `version` as
`null` even where the JSON specifies a value.

**c) `web/src/test/java/org/cbioportal/genome_nexus/web/CancerHotspotsIntegrationTest.java`**
(full HTTP integration test hitting the real `CancerHotspotsController` endpoints):
- Add test methods (following the existing `testSingleResidueHotspots`,
  `testTranscriptIdHotspots`, etc. naming/style) covering, per endpoint or at
  least for `genomic` and `transcript` (the two already most-tested):
  - `?version=v2` → only `v2`-tagged + legacy (no-version) hotspots come back.
  - `?version=v3` → `v2`, `v3`, and legacy hotspots all come back (everything).
  - no `version` param at all → identical result to `?version=v3` (confirms the
    default).
  - each returned hotspot's `version` field matches what was seeded, and is
    simply absent (not `null`) in the JSON for the legacy fixture rows.

**d) `service/src/test/java/org/cbioportal/genome_nexus/service/internal/VariantAnnotationServiceTest.java`**
— **found this is the existing test that actually exercises the `AnnotationController`
`fields=hotspots` path** (`getHotspotEnrichedAnnotationByVariantString`, lines
265-291). It mocks `CancerHotspotServiceImpl` directly via Mockito
(`cancerHotspotService.getHotspots(TranscriptConsequence, VariantAnnotation)`,
set up in `mockHotspotServiceMethods`, lines 378-393) and asserts the returned
`hotspotAnnotation.annotation` list equals `CancerHotspotMockData`'s mock data
exactly via `Hotspot.equals()`.
  - Because step 4 keeps `getHotspots(TranscriptConsequence, VariantAnnotation)`'s
    signature and behavior unchanged, **this existing test should keep passing
    with zero changes** — that's a direct regression check that requirement #1
    (hotspots field always returns both versions, unfiltered) still holds.
  - Extend `CancerHotspotMockData.java` (`service/src/test/java/.../mock/CancerHotspotMockData.java`)
    to set `version` on at least one of its mock `Hotspot` instances (e.g. the
    BRAF `V600` one), and add an assertion in this test that the `version` value
    survives all the way through to `annotation1.getHotspotAnnotation()`. This
    turns the existing implicit "both versions pass through" behavior into an
    explicit regression test rather than an assumption, and doubles as the
    `AnnotationController`-side version-exposure test called for in the
    requirements.

**e) New: `HotspotVersionTest.java`** (e.g.
`model/src/test/java/org/cbioportal/genome_nexus/model/HotspotVersionTest.java`
— no test currently exists for `AnnotationField` either, so this is a new but
low-risk addition): unit test `HotspotVersion.fromString("v2")`,
`fromString("V2")` (case-insensitivity), `fromString("v3")`, and
`fromString("bogus")` (expect the same `IllegalArgumentException` pattern as
`AnnotationField.fromString`).

**f) New: null-safe filter unit test**, wherever step 4's `isV2(Hotspot)`
predicate ends up living (`HotspotVersionTest.java` if it's on the enum, or a new
test method in `HotspotFilterTest.java` if it ends up there per the open
question below) — directly test the three cases that matter for backward
compatibility: `version="v2"` → true, `version="v3"` → false, `version=null` →
true. This is the single most important new test in this plan, since it's the
one piece of logic that silently breaks legacy data if gotten wrong.

**g) New: `HotspotTest.java`** (e.g.
`model/src/test/java/org/cbioportal/genome_nexus/model/HotspotTest.java` — also
doesn't currently exist): if `equals()`/`hashCode()` end up including `version`
(step 1), add a test asserting two `Hotspot`s with identical
`hugoSymbol`/`residue`/`type`/`tumorCount` but different `version` are *not*
equal, and that a `Hotspot` with `version=null` is still equal to itself /
behaves sanely in a `HashSet`/`LinkedHashSet` (used throughout
`CancerHotspotServiceImpl`).

**h) `web/src/main/java/org/cbioportal/genome_nexus/web/mixin/HotspotMixin.java`
(prod mixin, step 2) vs. the test-only one in (b)** — these are two separate
files with the same class name in different packages; make sure both get the
`version` field added, not just one (easy to fix one and forget the other since
they look identical at a glance).

**i) `HotspotFilterTest.java`** — no changes needed unless the null-safe
predicate from (f) is placed here; the existing position/mutation-type tests are
unaffected by `version` either way.

### 7. Docs
- `docs/API.md` currently has no hotspot-specific documentation to update, but if
  it documents the `fields` values or `/cancer_hotspots` endpoints elsewhere,
  update the description of the new `version` param there too.

## Open questions for implementation (non-blocking, can decide while coding)

- Exact enum constant naming (`HotspotVersion` vs. reusing a generic pattern) — no
  functional impact, will match existing `AnnotationField` conventions.
- Where exactly the null-safe `isV2()` predicate lives (`HotspotVersion` enum vs.
  `HotspotFilter` vs. a new small helper) — no functional impact, will follow
  whichever placement reads most naturally next to the existing `HotspotFilter`
  code once I'm in the files.

## Explicitly out of scope for this change

- No changes to `genome-nexus-importer` itself (data pipeline already supports v3
  as of the just-synced master).
- No changes to how/when genome-nexus's own data checkout/submodule gets bumped to
  pick up the new importer export — that's a separate deploy/ops concern.
- No live local MongoDB import/manual curl verification (per your answer, tests
  with updated fixtures are sufficient).
