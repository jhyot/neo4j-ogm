/*
 * Copyright (c) 2002-2018 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 *  conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package org.neo4j.ogm.session.request.strategy.impl;

import org.neo4j.ogm.session.request.strategy.MatchClauseBuilder;

/**
 * @author Frantisek Hartman
 * @author Michael J. Simons
 */
public class IdMatchClauseBuilder implements MatchClauseBuilder {

    @Override
    public String build(String label, boolean distinct) {
        String matchClauseFormat;
        if (label == null || label.isEmpty()) {
            matchClauseFormat = "MATCH (n) WHERE ID(n) = { id } WITH %sn";
        } else {
            matchClauseFormat = "MATCH (n:`" + label + "`) WHERE ID(n) = { id } WITH %sn";
        }
        return formatMatchClause(matchClauseFormat, distinct);
    }

    @Override
    public String build(String label, String property, boolean distinct) {

        String matchClauseFormat;
        if (label == null || label.isEmpty()) {
            matchClauseFormat  = "MATCH (n) WHERE n.`" + property + "` = { id } WITH %sn";
        } else {
            matchClauseFormat = "MATCH (n:`" + label + "`) WHERE n.`" + property + "` = { id } WITH %sn";
        }
        return formatMatchClause(matchClauseFormat, distinct);
    }

    private static String formatMatchClause(String matchClauseFormat, boolean distinct) {
        return String.format(matchClauseFormat, distinct ? "DISTINCT " : "");
    }
}
