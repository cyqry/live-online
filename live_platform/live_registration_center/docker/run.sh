#一键运行build
#首先要到Dockerfile所在目录的上一级目录，因为我们 docker build 是指定Dockerfile所在文件夹的
cd .. && docker build ./docker-live_registrationcenter -t live_registrationcenter \
&& docker run -it -p 7001:7001 --net live_net --name registrationcenter live_registrationcenter /bin/bash