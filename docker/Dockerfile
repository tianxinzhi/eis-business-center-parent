FROM 192.168.1.60:5000/oracle/serverjre:1.8.0_241-b07
MAINTAINER www.prolog-int.com
ARG RUN_ENV
ENV RUN_ENV=$RUN_ENV


RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' >/etc/timezone

RUN mkdir /usr/local/apps
RUN mkdir /usr/local/logs

COPY ./*.jar /usr/local/apps

COPY ./Entrypoint.sh /usr/local

RUN chmod +x /usr/local/Entrypoint.sh

WORKDIR /usr/local

ENTRYPOINT ["/usr/local/Entrypoint.sh"]
CMD ["${RUN_ENV}"]