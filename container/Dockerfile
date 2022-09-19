FROM ubuntu:18.04

LABEL maintainer="Adam Price (pricea2@mskcc.org)" \
      contributor="Anne Marie Noronha (noronhaa@mskcc.org)" \
      version.image="0.0.1" \
      version.annotation-tools="1.0.0" \
      version.annotation-pipeline="1.0.0" \
      version.python="3.9.7" \
      version.java="1.8.0" \
      version.maven="3.8.4" \
      source.annotation-tools="https://github.com/genome-nexus/annotation-tools" \
      source.annotation-pipeline="https://github.com/genome-nexus/genome-nexus-annotation-pipeline" \
      source.maven="https://apache.osuosl.org/maven/maven-3/3.8.4/binaries/"


ARG USER_HOME_DIR="/root"

ENV ANNO_TOOLS_VERSION 1.0.0
ENV ANNO_PIPELINE_VERSION 1.0.0

# Install Python3.
RUN apt-get update --fix-missing && apt-get install -y \
    tcsh \
    libnss-sss \
    git \
    curl \
    python3 \
    python3-pip \
    python3-dev \
    python3-distutils \
    python3-apt \
    build-essential \
    python3-chardet \
    wget \
    && pip3 --no-cache-dir install --upgrade pip \
    && apt-get clean && apt-get purge \
    && rm -rf /var/lib/apt/lists/* /tmp/* /var/tmp/*


RUN apt-get update && apt-get install -y libnet-ssleay-perl libcrypt-ssleay-perl libextutils-pkgconfig-perl


# Install OpenJDK-8
RUN apt-get update && \
    apt-get install -y openjdk-8-jdk && \
    apt-get install -y ant && \
    apt-get clean;
    
# Fix java certificate issues
RUN apt-get update && \
    apt-get install ca-certificates-java && \
    apt-get clean && \
    update-ca-certificates -f;

# Setup JAVA_HOME -- useful for docker commandline
ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64/
RUN export JAVA_HOME


#Install Maven.
RUN mkdir -p /usr/share/maven /usr/share/maven/ref \
 && curl -fsSL -o /tmp/apache-maven.tar.gz https://apache.osuosl.org/maven/maven-3/3.8.5/binaries/apache-maven-3.8.5-bin.tar.gz \
 && tar -xzf /tmp/apache-maven.tar.gz -C /usr/share/maven --strip-components=1 \
 && rm -f /tmp/apache-maven.tar.gz \
 && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

ENV MAVEN_HOME /usr/share/maven
ENV MAVEN_CONFIG "$USER_HOME_DIR/.m2"

CMD ["mvn", "--version"]

RUN git clone https://github.com/genome-nexus/annotation-tools
RUN git clone https://github.com/genome-nexus/genome-nexus-annotation-pipeline


WORKDIR "/genome-nexus-annotation-pipeline"
RUN cp /genome-nexus-annotation-pipeline/annotationPipeline/src/main/resources/application.properties.EXAMPLE /genome-nexus-annotation-pipeline/annotationPipeline/src/main/resources/application.properties
RUN cp /genome-nexus-annotation-pipeline/annotationPipeline/src/main/resources/log4j.properties.EXAMPLE /genome-nexus-annotation-pipeline/annotationPipeline/src/main/resources/log4j.properties

# set log4j file in properties
RUN sed -i "s|log4j\.appender\.a\.File.*|log4j.appender.a.File = /out/genome-nexus-annotation-pipeline.log|" /genome-nexus-annotation-pipeline/annotationPipeline/src/main/resources/log4j.properties

#Run maven on nexus pipeline.
RUN /usr/share/maven/bin/mvn clean install

RUN cp /genome-nexus-annotation-pipeline/annotationPipeline/target/annotationPipeline-1.0.0.jar /annotation-tools/annotator.jar

WORKDIR "/"
COPY annotation_suite_wrapper.sh /annotation-tools/annotation_suite_wrapper.sh
COPY run_script.sh /genome-nexus-annotation-pipeline/run_script.sh
RUN chmod 755 /genome-nexus-annotation-pipeline/run_script.sh
ENTRYPOINT ["/genome-nexus-annotation-pipeline/run_script.sh"]

# Add filter script
#COPY filter-somatic-maf.R /usr/bin
#COPY filter-germline-maf.R /usr/bin
#RUN chmod 755 /usr/bin/filter-somatic-maf.R
#RUN chmod 755 /usr/bin/filter-germline-maf.R


