/*
 *  com.github.rutledgepaulv.rqe.pipes.StandardFieldTest
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
import com.github.rutledgepaulv.rqe.testsupport.User;
import org.junit.Test;

public class StandardFieldTest extends TestBase {

    static {
        MODE = RunMode.TEST;
    }

    private QueryConversionPipeline pipeline = QueryConversionPipeline.defaultPipeline();

    @Test
    public void stringPropertyOnRootObject() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("firstName==Paul", User.class);

        assertPredicate(condition, new User().setFirstName("Paul"));

        assertNotPredicate(condition, new User().setFirstName("Joe"));

        assertMongo(condition, "{\"firstName\": \"Paul\"}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"firstName\" : {\n" +
            "      \"value\" : \"Paul\",\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");
    }


    @Test
    public void stringPropertyOnRootObjectRegex() {

        Condition<GeneralQueryBuilder> condition1 = pipeline.apply("firstName=re=.*Paul$", User.class);

        assertPredicate(condition1, new User().setFirstName("Paul"));

        assertNotPredicate(condition1, new User().setFirstName("Joe"));

        assertMongo(condition1, "{\"firstName\": {\"$regex\": \".*Paul$\", \"$options\": \"\"}}");

        assertElasticsearch(condition1, "{\n" +
            "  \"regexp\" : {\n" +
            "    \"firstName\" : {\n" +
            "      \"value\" : \".*Paul$\",\n" +
            "      \"flags_value\" : 65535,\n" +
            "      \"max_determinized_states\" : 10000,\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");
    }

    @Test
    public void numberPropertyOnRootObject() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("age==23", User.class);

        assertPredicate(condition, new User().setAge(23));

        assertNotPredicate(condition, new User().setAge(24));

        assertMongo(condition, "{\"age\": 23}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"age\" : {\n" +
            "      \"value\" : 23,\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");

    }

    @Test
    public void booleanPropertyOnRootObject() {

        Condition<GeneralQueryBuilder> condition = pipeline.apply("enabled==true", User.class);

        assertPredicate(condition, new User().setEnabled(true));

        assertNotPredicate(condition, new User().setEnabled(false));

        assertMongo(condition, "{\"enabled\": true}");

        assertElasticsearch(condition, "{\n" +
            "  \"term\" : {\n" +
            "    \"enabled\" : {\n" +
            "      \"value\" : true,\n" +
            "      \"boost\" : 1.0\n" +
            "    }\n" +
            "  }\n" +
            "}");

    }

}
