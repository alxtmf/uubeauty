/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ru.p03.uubeauty.model.repository;

import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import ru.p03.uubeauty.AppEnv;
import ru.p03.uubeauty.EmployeeSender;
import ru.p03.uubeauty.model.ClsEmployee;

/**
 *
 * @author altmf
 */
public class RepoTest {

    private Logger log = Logger.getLogger(RepoTest.class.getName());

    public RepoTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
        AppEnv.getContext();
    }

    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void testIsFree() {
        ClsEmployee find = AppEnv.getContext().getClassifierRepository().find(ClsEmployee.class, 1L);
        if (find != null) {
            RegScheduleRepositoryImpl regScheduleRepository = AppEnv.getContext().getRegScheduleRepository();
            LocalDateTime ldt = LocalDateTime.of(2017, 11, 15, 17, 0);
            boolean free = regScheduleRepository.isFree(find, ldt);
            log.log(Level.SEVERE, "2017, 11, 15, 17, 0 = " + free);

            ldt = LocalDateTime.of(2017, 11, 15, 18, 0);
            free = regScheduleRepository.isFree(find, ldt);
            log.log(Level.SEVERE, "2017, 11, 15, 18, 0 = " + free);
        }
    }
}
