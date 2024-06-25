package at.ac.tuwien.sepr.groupphase.backend;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
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
}
