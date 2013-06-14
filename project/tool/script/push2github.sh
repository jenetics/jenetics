#!/bin/bash

hg bookmark -f default
hg push github
hg bookmarks -d default