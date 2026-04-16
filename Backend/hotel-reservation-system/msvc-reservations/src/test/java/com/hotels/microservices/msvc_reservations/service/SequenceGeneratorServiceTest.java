package com.hotels.microservices.msvc_reservations.service;

import com.hotels.microservices.msvc_reservations.model.Counter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SequenceGeneratorServiceTest {


    @Mock
    private MongoOperations mongoOperations;


    @InjectMocks
    private SequenceGeneratorService sequenceGeneratorService;

    @Test
    void shouldReturnIncrementedSequence_whenGenerateSequenceIsCalled() {

        String seqName = "reservationOrder";
        long expectedSequence = 1L;
        Counter mockCounter = new Counter();
        mockCounter.setSeq(expectedSequence);

        given(mongoOperations.findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(Counter.class)
        )).willReturn(mockCounter);


        long result = sequenceGeneratorService.generateSequence(seqName);

        assertEquals(expectedSequence, result);

        verify(mongoOperations).findAndModify(
                any(Query.class),
                any(Update.class),
                any(FindAndModifyOptions.class),
                eq(Counter.class)
        );
    }
}