/**
 * Copyright (c) 2008-2010, http://code.google.com/p/snakeyaml/
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.yaml.snakeyaml.issues.issue68;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.YamlDocument;

public class NonAsciiCharacterTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testLoad() {
        Yaml yaml = new Yaml();
        Map<String, Map<String, String>> obj = (Map<String, Map<String, String>>) yaml
                .load("test.string: {en: И}");
        assertEquals(1, obj.size());
        assertEquals("Map: " + obj.toString(), "И", obj.get("test.string").get("en"));
    }

    public void testLoadFromFileWithWrongEncoding() {
        try {
            Yaml yaml = new Yaml();
            Object text = yaml.load(new InputStreamReader(new FileInputStream(
                    "src/test/resources/issues/issue68.txt"), "Cp1252"));
            fail("Invalid UTF-8 must not be aceepted: " + text.toString());
        } catch (Exception e) {
            assertEquals("special characters are not allowed", e.getMessage());
        }
    }

    public void testLoadFromFile() throws UnsupportedEncodingException, FileNotFoundException {
        Yaml yaml = new Yaml();
        String text = (String) yaml.load(new InputStreamReader(new FileInputStream(
                "src/test/resources/issues/issue68.txt"), "UTF-8"));
        assertEquals("И жить торопится и чувствовать спешит...", text);
    }

    public void testLoadFromInputStream() {
        InputStream input;
        input = YamlDocument.class.getClassLoader().getResourceAsStream("issues/issue68.txt");
        if (input == null) {
            throw new RuntimeException("Can not find issues/issue68.txt");
        }
        Yaml yaml = new Yaml();
        String text = (String) yaml.load(input);// UTF-8 by default
        assertEquals("И жить торопится и чувствовать спешит...", text);
    }
}
