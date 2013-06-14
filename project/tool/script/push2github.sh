#!/bin/bash

hg bookmark -f default
hg push gitbub
hg bookmarks -d default