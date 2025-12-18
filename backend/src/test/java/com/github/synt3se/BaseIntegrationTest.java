package com.github.synt3se;

import com.github.synt3se.entity.*;
import com.github.synt3se.repository.*;
import com.github.synt3se.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected JwtTokenProvider jwtTokenProvider;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected ChildRepository childRepository;

    @Autowired
    protected BranchRepository branchRepository;

    @Autowired
    protected CourseRepository courseRepository;

    @Autowired
    protected LessonRepository lessonRepository;

    @Autowired
    protected AttendanceRepository attendanceRepository;

    @Autowired
    protected GradeRepository gradeRepository;

    @Autowired
    protected PaymentRepository paymentRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

    // Тестовые данные
    protected Branch testBranch;
    protected Course testCourse;
    protected User parentUser;
    protected User teacherUser;
    protected Child testChild;
    protected Lesson testLesson;
    protected String parentToken;
    protected String teacherToken;

    @BeforeEach
    void setUpTestData() {
        // Создаём филиал
        testBranch = Branch.builder()
                .name("Тестовый филиал")
                .address("ул. Тестовая, д. 1")
                .pricePerLesson(new BigDecimal("1000"))
                .pricePerMonth(new BigDecimal("8000"))
                .pricePerYear(new BigDecimal("80000"))
                .build();
        branchRepository.save(testBranch);

        // Создаём курс
        testCourse = Course.builder()
                .name("Тестовый курс")
                .build();
        courseRepository.save(testCourse);

        // Создаём учителя
        teacherUser = User.builder()
                .fullName("Тестовый Учитель")
                .email("teacher@test.com")
                .phone("+7 999 111-11-11")
                .password(passwordEncoder.encode("password"))
                .role(Role.TEACHER)
                .branch(testBranch)
                .build();
        userRepository.save(teacherUser);

        // Создаём родителя
        parentUser = User.builder()
                .fullName("Тестовый Родитель")
                .email("parent@test.com")
                .phone("+7 999 222-22-22")
                .password(passwordEncoder.encode("password"))
                .role(Role.PARENT)
                .branch(testBranch)
                .build();
        userRepository.save(parentUser);

        // Создаём ребёнка
        Set<Course> courses = new HashSet<>();
        courses.add(testCourse);

        testChild = Child.builder()
                .fullName("Тестовый Ребёнок")
                .birthDate(LocalDate.now().minusYears(10))
                .parent(parentUser)
                .branch(testBranch)
                .courses(courses)
                .build();
        childRepository.save(testChild);

        // Создаём занятие на завтра
        testLesson = Lesson.builder()
                .startTime(LocalDateTime.now().plusDays(1).withHour(10).withMinute(0))
                .endTime(LocalDateTime.now().plusDays(1).withHour(11).withMinute(30))
                .course(testCourse)
                .branch(testBranch)
                .teacher(teacherUser)
                .status(LessonStatus.SCHEDULED)
                .topic("Тестовое занятие")
                .build();
        lessonRepository.save(testLesson);

        // Записываем ребёнка на занятие
        Attendance attendance = Attendance.builder()
                .lesson(testLesson)
                .child(testChild)
                .build();
        attendanceRepository.save(attendance);

        // Генерируем токены
        parentToken = jwtTokenProvider.generateToken(parentUser.getId(), parentUser.getEmail(), String.valueOf(parentUser.getRole()));
        teacherToken = jwtTokenProvider.generateToken(teacherUser.getId(), teacherUser.getEmail(), String.valueOf(teacherUser.getRole()));
    }

    protected String asJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }
}
