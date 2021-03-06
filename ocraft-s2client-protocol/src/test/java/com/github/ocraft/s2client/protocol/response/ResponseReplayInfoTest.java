package com.github.ocraft.s2client.protocol.response;

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

import SC2APIProtocol.Sc2Api;
import com.github.ocraft.s2client.protocol.game.GameStatus;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Optional;
import java.util.stream.Stream;

import static com.github.ocraft.s2client.protocol.Constants.nothing;
import static com.github.ocraft.s2client.protocol.Fixtures.*;
import static com.github.ocraft.s2client.protocol.response.ResponseReplayInfo.Error.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.params.provider.Arguments.of;

class ResponseReplayInfoTest {

    @Test
    void throwsExceptionWhenSc2ApiResponseDoesNotHaveReplayInfo() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ResponseReplayInfo.from(nothing()))
                .withMessage("provided argument doesn't have replay info response");

        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ResponseReplayInfo.from(aSc2ApiResponse().build()))
                .withMessage("provided argument doesn't have replay info response");
    }

    @Test
    void convertsSc2ApiResponseReplayInfoToResponseReplayInfo() {
        ResponseReplayInfo responseReplayInfo = ResponseReplayInfo.from(sc2ApiResponseWithReplayInfo());

        assertThatResponseDoesNotHaveError(responseReplayInfo);
        assertThatResponseIsInValidState(responseReplayInfo);
    }

    private void assertThatResponseDoesNotHaveError(ResponseReplayInfo responseReplayInfo) {
        assertThat(responseReplayInfo.getError())
                .as("response replay info doesn't have error")
                .isEmpty();

        assertThat(responseReplayInfo.getErrorDetails())
                .as("response replay info doesn't have error details")
                .isEmpty();

        assertThat(responseReplayInfo.getReplayInfo())
                .as("response replay info doesn't have error details")
                .isNotEmpty();
    }

    private void assertThatResponseIsInValidState(ResponseReplayInfo responseReplayInfo) {
        assertThat(responseReplayInfo).as("converted response replay info").isNotNull();
        assertThat(responseReplayInfo.getType()).as("type of replay info response")
                .isEqualTo(ResponseType.REPLAY_INFO);
        assertThat(responseReplayInfo.getStatus()).as("status of replay info response")
                .isEqualTo(GameStatus.LAUNCHED);
    }

    @Test
    void throwsExceptionWhenMapInfoIsNotProvided() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ResponseReplayInfo.from(sc2ApiResponseReplayInfoWithoutMap()))
                .withMessage("map info (local or battlenet) is required");
    }

    private Sc2Api.Response sc2ApiResponseReplayInfoWithoutMap() {
        return aSc2ApiResponse().setReplayInfo(without(
                () -> sc2ApiResponseReplayInfo().toBuilder(),
                Sc2Api.ResponseReplayInfo.Builder::clearLocalMapPath,
                Sc2Api.ResponseReplayInfo.Builder::clearMapName
        ).build()).build();
    }

    @ParameterizedTest(name = "\"{0}\" is mapped to {1}")
    @MethodSource(value = "responseReplayInfoErrorMappings")
    void mapsSc2ApiResponseGameError(
            Sc2Api.ResponseReplayInfo.Error sc2ApiResponseReplayInfoError,
            ResponseReplayInfo.Error expectedResponseReplayInfoError) {
        assertThat(ResponseReplayInfo.Error.from(sc2ApiResponseReplayInfoError))
                .isEqualTo(expectedResponseReplayInfoError);
    }

    private static Stream<Arguments> responseReplayInfoErrorMappings() {
        return Stream.of(
                of(Sc2Api.ResponseReplayInfo.Error.MissingReplay, MISSING_REPLAY),
                of(Sc2Api.ResponseReplayInfo.Error.InvalidReplayPath, INVALID_REPLAY_PATH),
                of(Sc2Api.ResponseReplayInfo.Error.InvalidReplayData, INVALID_REPLAY_DATA),
                of(Sc2Api.ResponseReplayInfo.Error.ParsingError, PARSING_ERROR),
                of(Sc2Api.ResponseReplayInfo.Error.DownloadError, DOWNLOAD_ERROR));
    }

    @Test
    void throwsExceptionWhenResponseReplayInfoErrorIsNull() {
        assertThatExceptionOfType(IllegalArgumentException.class)
                .isThrownBy(() -> ResponseReplayInfo.Error.from(nothing()))
                .withMessage("sc2api response replay info error is required");
    }

    @Test
    void convertsSc2ApiResponseReplayInfoWithErrorToResponseReplayInfo() {
        ResponseReplayInfo responseReplayInfo = ResponseReplayInfo.from(sc2ApiResponseWithReplayInfoWithError());

        assertThatResponseIsInValidState(responseReplayInfo);
        assertThatErrorsAreMapped(responseReplayInfo);
    }

    private void assertThatErrorsAreMapped(ResponseReplayInfo responseReplayInfo) {
        assertThat(responseReplayInfo.getError())
                .as("response replay info: error")
                .isEqualTo(Optional.of(ResponseReplayInfo.Error.INVALID_REPLAY_PATH));

        assertThat(responseReplayInfo.getErrorDetails())
                .as("response replay info: error details")
                .isEqualTo(Optional.of(ERROR_REPLAY_INFO));

        assertThat(responseReplayInfo.getReplayInfo())
                .as("response replay info: replay info")
                .isEmpty();
    }

    @Test
    void fulfillsEqualsContract() {
        EqualsVerifier.forClass(ResponseReplayInfo.class)
                .withIgnoredFields("nanoTime")
                .withNonnullFields("type", "status")
                .withRedefinedSuperclass()
                .verify();
    }

}
