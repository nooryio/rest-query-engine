/*
 *  com.github.rutledgepaulv.rqe.pipes.NestedObjectTest
 *  *
 *  * Copyright (C) 2016 Paul Rutledge <paul.v.rutledge@gmail.com>
 *  *
 *  * This software may be modified and distributed under the terms
 *  * of the MIT license.  See the LICENSE file for details.
 *
 */

package com.github.rutledgepaulv.rqe.pipes;

import com.github.rutledgepaulv.qbuilders.builders.GeneralQueryBuilder;
import com.github.rutledgepaulv.qbuilders.conditions.Condition;
import com.github.rutledgepaulv.rqe.testsupport.State;
import com.github.rutledgepaulv.rqe.testsupport.User;
import org.junit.Test;

public class NestedObjectTest extends TestBase {

    static {
        MODE = RunMode.TEST;
    }

    private QueryConversionPipeline pipeline = QueryConversionPipeline.defaultPipeline();

    @Test
    public void stringPropertyOnNestedObject() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("address.street=='1 Michigan Ave'", User.class);

        User user = new User();

        user.getAddress().setStreet("1 Michigan Ave");
        assertPredicate(condition, user);

        user.getAddress().setStreet("Something else");
        assertNotPredicate(condition, user);

        assertMongo(condition, "{\"address.street\": \"1 Michigan Ave\"}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"address.street\" : {\n" +
            "      \"value\" : \"1 Michigan Ave\",\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");

    }

    @Test
    public void numberPropertyOnNestedObject() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("address.unit==100", User.class);

        User user = new User();

        user.getAddress().setUnit(100);
        assertPredicate(condition, user);

        user.getAddress().setUnit(400);
        assertNotPredicate(condition, user);

        assertMongo(condition, "{\"address.unit\": 100}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"address.unit\" : {\n" +
            "      \"value\" : 100,\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");

    }

    @Test
    public void booleanPropertyOnNestedObject() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("address.hasCat==false", User.class);

        User user = new User();

        user.getAddress().setHasCat(false);
        assertPredicate(condition, user);

        user.getAddress().setHasCat(true);
        assertNotPredicate(condition, user);

        assertMongo(condition, "{\"address.hasCat\": false}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"address.hasCat\" : {\n" +
            "      \"value\" : false,\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");

    }


    @Test
    public void enumPropertyOnNestedObject_mongodbAndElasticsearch() {
        Condition<GeneralQueryBuilder> condition = pipeline.apply("address.state==ILLINOIS", User.class);

        assertMongo(condition, "{\"address.state\": \"ILLINOIS\"}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"address.state\" : {\n" +
            "      \"value\" : \"ILLINOIS\",\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");
    }


    @Test
    public void enumPropertyOnNestedObject_predicate() {
        Condition<GeneralQueryBuilder> condition = pipeline.apply("address.state==ILLINOIS", User.class);

        User user = new User();

        user.getAddress().setState(State.ILLINOIS);
        assertPredicate(condition, user);

        user.getAddress().setState(State.MINNESOTA);
        assertNotPredicate(condition, user);
    }

}
