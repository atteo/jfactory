#!/bin/bash
/usr/local/bin/install-plugins.sh $(cat plugins.txt | grep -v "^#")
