#!/bin/bash

java -cp org.jenetix/build/libs/org.jenetix-jarjar-3.0.0-SNAPSHOT.jar org.jenetics.internal.util.DieHarder org.jenetix.random.MRG2Random -a
java -cp org.jenetix/build/libs/org.jenetix-jarjar-3.0.0-SNAPSHOT.jar org.jenetics.internal.util.DieHarder org.jenetix.random.MRG3Random -a
java -cp org.jenetix/build/libs/org.jenetix-jarjar-3.0.0-SNAPSHOT.jar org.jenetics.internal.util.DieHarder org.jenetix.random.MRG4Random -a
java -cp org.jenetix/build/libs/org.jenetix-jarjar-3.0.0-SNAPSHOT.jar org.jenetics.internal.util.DieHarder org.jenetix.random.MRG5Random -a
