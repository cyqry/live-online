#一键运行build
#首先要到Dockerfile所在目录的上一级目录，因为我们 docker build 是指定Dockerfile所在文件夹的
cd .. && docker build ./docker-live_gateway -t live_gateway \
&& docker run -it -p 443:443 --net live_net --name gateway live_gateway /bin/bash