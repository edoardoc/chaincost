# tag::kernel-slim[]
FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi
# end::kernel-slim[]

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

LABEL \
  org.opencontainers.image.authors="Your Name" \
  org.opencontainers.image.vendor="Open Liberty" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/OpenLiberty/guide-containerize" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
# tag::name[]
  name="fetcher" \
# end::name[]
  version="$VERSION-$REVISION" \
# tag::summary[]
  summary="The fetcher microservice from the Containerizing microservices guide" \
  description="This image contains the fetcher microservice running with the Open Liberty runtime."
# end::summary[]

COPY --chown=1001:0 src/main/liberty/config /config/

# tag::features[]
RUN features.sh
# end::features[]

# tag::copy-war[]
COPY --chown=1001:0 target/guide-containerize-fetcher.war /config/apps
# end::copy-war[]

RUN configure.sh
