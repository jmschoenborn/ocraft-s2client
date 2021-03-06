package com.github.ocraft.s2client.protocol.game;

/*-
 * #%L
 * ocraft-s2client-protocol
 * %%
 * Copyright (C) 2017 - 2018 Ocraft Project
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static com.github.ocraft.s2client.protocol.Constants.nothing;
import static com.github.ocraft.s2client.protocol.Fixtures.DATA_IN_BYTES;
import static com.github.ocraft.s2client.protocol.Fixtures.LOCAL_MAP_PATH;
import static java.util.Collections.nCopies;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

class LocalMapTest {

    @Test
    void createsInstanceWithPath() {
        Path localMapPath = Paths.get(LOCAL_MAP_PATH);
        LocalMap localMap = LocalMap.of(localMapPath);

        assertThat(localMap.getPath()).isEqualTo(Optional.of(localMapPath));
        assertThat(localMap.getDataInBytes()).isEqualTo(Optional.empty());
    }

    @Test
    void createsInstanceWithDataInBytes() {
        LocalMap localMap = LocalMap.of(DATA_IN_BYTES);

        assertThat(localMap.getPath()).isEqualTo(Optional.empty());
        assertThat(localMap.getDataInBytes()).isEqualTo(Optional.of(DATA_IN_BYTES));
    }

    @Test
    void createsInstanceWithPathAndDataInBytes() {
        Path localMapPath = Paths.get(LOCAL_MAP_PATH);
        LocalMap localMap = LocalMap.of(localMapPath, DATA_IN_BYTES);

        assertThat(localMap.getPath()).isEqualTo(Optional.of(localMapPath));
        assertThat(localMap.getDataInBytes()).isEqualTo(Optional.of(DATA_IN_BYTES));
    }

    @Test
    void throwsExceptionWhenAnyOfArgumentInFactoryMethodIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> LocalMap.of((Path) nothing()))
                .withMessage("path is required");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> LocalMap.of((byte[]) nothing()))
                .withMessage("data in bytes is required");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> LocalMap.of(nothing(), DATA_IN_BYTES))
                .withMessage("path is required");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> LocalMap.of(Paths.get(LOCAL_MAP_PATH), nothing()))
                .withMessage("data in bytes is required");
    }

    @Test
    void throwsExceptionWhenPathLengthIsExceeded() {
        Path tooLongPath = Paths.get(String.join("", nCopies(261, String.valueOf('a'))));
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> LocalMap.of(tooLongPath))
                .withMessage("Maximum length of path (260) exceeded. Actual was 261.");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> LocalMap.of(tooLongPath, DATA_IN_BYTES))
                .withMessage("Maximum length of path (260) exceeded. Actual was 261.");
    }

    @Test
    void fulfillsEqualsContract() {
        EqualsVerifier.forClass(LocalMap.class).verify();
    }

}
