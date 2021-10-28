FROM jfactory/java16-slave

USER root

RUN apt update &&\
    apt-get install -y wget gnupg2 &&\
    wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - &&\
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list &&\
    apt update &&\
    apt-get install -y google-chrome-stable firefox &&\
    rm -rf /var/lib/apt/lists/*

USER jenkins
