#!/bin/sh

echo 'hook: formatting files using :spotlessApply'

./gradlew spotlessApply

git add --all

echo 'hook: formatting complete'
