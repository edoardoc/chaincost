# tag::kernel-slim[]
FROM icr.io/appcafe/open-liberty:kernel-slim-java11-openj9-ubi
# end::kernel-slim[]

ARG VERSION=1.0
ARG REVISION=SNAPSHOT

# tag::label[]
LABEL \
  org.opencontainers.image.authors="Your Name" \
  org.opencontainers.image.vendor="Open Liberty" \
  org.opencontainers.image.url="local" \
  org.opencontainers.image.source="https://github.com/OpenLiberty/guide-containerize" \
  org.opencontainers.image.version="$VERSION" \
  org.opencontainers.image.revision="$REVISION" \
  vendor="Open Liberty" \
  name="summer" \
  version="$VERSION-$REVISION" \
  summary="The summer microservice from the Containerizing microservices guide" \
  description="This image contains the summer microservice running with the Open Liberty runtime."
# end::label[]

# tag::copy-config[]
# tag::config-userID[]
COPY --chown=1001:0 \
# end::config-userID[]
    # tag::summer-config[]
    src/main/liberty/config \
    # end::summer-config[]
    # tag::config[]
    /config/
    # end::config[]
# end::copy-config[]

# tag::features[]
RUN features.sh
# end::features[]

# tag::copy-war[]
# tag::war-userID[]
COPY --chown=1001:0 \
# end::war-userID[]
    # tag::summer-war[]
    target/guide-containerize-summer.war \
    # end::summer-war[]
    # tag::config-apps[]
    /config/apps
    # end::config-apps[]
# end::copy-war[]

# tag::configure-sh[]
RUN configure.sh
# end::configure-sh[]
