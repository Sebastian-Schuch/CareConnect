package at.ac.tuwien.sepr.groupphase.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TestBase {

    @Autowired
    DataGenerator dataGenerator;


    @BeforeEach
    public void setupDb() {
        dataGenerator.generateData();
    }

    @AfterEach
    public void tearDownDb() {
        dataGenerator.clearData();
    }
}
