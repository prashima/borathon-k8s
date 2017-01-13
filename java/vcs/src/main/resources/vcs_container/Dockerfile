FROM vmware/photon
COPY vcs/bin /vcs/bin
COPY vcs/lib /vcs/lib
COPY vcs/config /vcs/config
COPY vcs/scripts /vcs/scripts
COPY vcs/demo /vcs/demo

RUN tdnf install -y openjdk &&\
    tdnf install -y docker &&\
    tdnf install -y perl &&\
    tdnf install -y sed

EXPOSE 19000
ENTRYPOINT ["/vcs/scripts/start_vcs.sh"]
