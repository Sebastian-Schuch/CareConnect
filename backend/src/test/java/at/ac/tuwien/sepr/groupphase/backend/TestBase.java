package at.ac.tuwien.sepr.groupphase.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class TestBase {

    @Autowired
    DataGenerator dataGenerator;

    private final String dataType;

    public TestBase(String dataType) {
        this.dataType = dataType;
    }


    @BeforeEach
    public void setupDb() {
        dataGenerator.generateData(dataType);
    }

    @AfterEach
    public void tearDownDb() {
        dataGenerator.clearData();
    }
}
