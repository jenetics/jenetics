/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jenetics.example.image;

import org.jenetics.Gene;
import org.jenetics.util.ISeq;
import org.jenetics.util.MSeq;

/**
 * Represents a fixed size polygon with its fill color.
 */
public class PolygonGene implements Gene<Polygon, PolygonGene> {

    private final Polygon polygon;

    static ISeq<PolygonGene> seq(final int polygonCount, final int polygonLength) {
      return MSeq.<PolygonGene>ofLength(polygonCount)
        .fill(() -> new PolygonGene(Polygon.randomPolygon(polygonLength)))
        .toISeq();
    }

    private PolygonGene(final Polygon p) {
      this.polygon = p;
    }

    @Override
    public boolean isValid() {
      return true;
    }

    @Override
    public Polygon getAllele() {
      return polygon;
    }

    @Override
    public PolygonGene newInstance() {
      return new PolygonGene( Polygon.randomPolygon( polygon.getLength() ) );
    }

    @Override
    public PolygonGene newInstance( Polygon value ) {
      return new PolygonGene( value );
    }
}
