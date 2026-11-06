import logging

logger = logging.getLogger("alphagenome-sidecar")


def log_debug(phase: str, message: str) -> None:
    logger.info(f"[{phase}] {message}")
