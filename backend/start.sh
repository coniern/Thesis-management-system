#!/bin/bash

# 进入脚本所在目录
cd "$(dirname "$0")"

# 启动后端服务
mvn spring-boot:run