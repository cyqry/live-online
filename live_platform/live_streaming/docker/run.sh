#一键运行build
#首先要到Dockerfile所在目录的上一级目录，因为我们 docker build 是指定Dockerfile所在文件夹的
cd .. && docker build ./docker-live_streaming -t live_streaming \
&& docker run -it --net live_net --name streaming live_streaming /bin/bash