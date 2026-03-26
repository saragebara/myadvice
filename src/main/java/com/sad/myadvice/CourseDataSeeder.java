package com.sad.myadvice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sad.myadvice.entity.Course;
import com.sad.myadvice.entity.CourseProgram;
import com.sad.myadvice.entity.Major;
import com.sad.myadvice.entity.Prerequisite;
import com.sad.myadvice.repository.CourseProgramRepository;
import com.sad.myadvice.repository.CourseRepository;
import com.sad.myadvice.repository.PrerequisiteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/*
DATA SEEDER FOR COURSES AVAILABLE AND THEIR DESCRIPTIONS. ORDER 2
*/

@Component
@Order(2) //Runs after DataSeeder.java
public class CourseDataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final CourseProgramRepository courseProgramRepository;
    private final PrerequisiteRepository prerequisiteRepository;

    public CourseDataSeeder(CourseRepository courseRepository,
                            CourseProgramRepository courseProgramRepository,
                            PrerequisiteRepository prerequisiteRepository) {
        this.courseRepository = courseRepository;
        this.courseProgramRepository = courseProgramRepository;
        this.prerequisiteRepository = prerequisiteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        //only seeds if the course table is empty
        if (courseRepository.count() > 0) return;

        //building map of course descriptions
        java.util.Map<String, String> descriptions = buildDescriptionMap();

        //initializing 
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = new ClassPathResource("courses.json").getInputStream();
        JsonNode root = mapper.readTree(is);
        JsonNode courses = root.get("courses");

        //FIRST PASS: Creating all the courses 
        for (JsonNode node : courses) {
            String code = node.get("course code").asText().trim();
            String name = node.get("course name").asText().trim();

            //year level based on course code
            int yearLevel = getYearLevel(code);
            //course category
            Course.Category category = getCategory(code);
            //creating a new course
            Course course = new Course();
            course.setCode(code);
            course.setName(name);
            course.setCredits(3); //assuming that all courses are 3 credits for MVP
            course.setYearLevel(yearLevel);
            course.setCategory(category);
            course.setRequired(false); //initially false, set via CourseProgram
            //returns the courses description or defaults to no description if not available
            course.setDescription(descriptions.getOrDefault(code, "No description available."));

            //offering terms
            boolean fall = node.get("fall").asBoolean();
            boolean winter = node.get("winter").asBoolean();
            boolean summer = node.get("summer").asBoolean();
            course.setOfferedFall(fall);
            course.setOfferedWinter(winter);
            course.setOfferedSummer(summer);

            courseRepository.save(course);

            //CourseProgram entries ----------------------------------------
            JsonNode requiredBy = node.get("required");
            if (requiredBy != null) {
                for (JsonNode majorNode : requiredBy) {
                    String majorCode = majorNode.asText().trim();
                    Major major = Major.fromCode(majorCode);
                    if (major != null) {
                        CourseProgram cp = new CourseProgram();
                        cp.setCourse(course);
                        cp.setMajor(major);
                        courseProgramRepository.save(cp);
                    }
                }
            }
        }

        System.out.println("YAY Courses seeded successfuly: " + courseRepository.count());

        //SECOND PASS: Seed prerequisites ----------------------------------------------------
        //HAS to be in second pass, all courses must exist before adding prereq info
        for (JsonNode node : courses) {
            String courseCode = node.get("course code").asText().trim();
            Course course = courseRepository.findByCode(courseCode);
            if (course == null) continue;

            JsonNode prereqs = node.get("prerequisites");
            if (prereqs == null || prereqs.isEmpty()) continue;

            for (JsonNode prereqNode : prereqs) {
                String prereqStr = prereqNode.asText().trim();
                if (prereqStr.isEmpty()) continue;

                //handling the "OR" prerequisites, ex) "COMP-1000 or MATH-1720"
                parsePrerequisiteString(course, prereqStr);
            }
        }

        System.out.println("YAY Prerequisites seeded successfully");
    }

    //parsing prerequisite strings -------------------------------------------
    private void parsePrerequisiteString(Course course, String prereqStr) {
        //skipping prereqs that don't apply for undergrad/specific case
        if (prereqStr.startsWith("Other") || prereqStr.startsWith("Ontario") || prereqStr.startsWith("MATH-1280")) {
            return;
        }

        //handling OR prereqs
        //using CO_REC
        if (prereqStr.toLowerCase().contains(" or ")) {
            String[] options = prereqStr.split("(?i) or ");
            List<Course> orCourses = new ArrayList<>();
            for (String option : options) {
                String code = extractCourseCode(option.trim());
                if (code != null) {
                    Course req = courseRepository.findByCode(code);
                    if (req != null) orCourses.add(req);
                }
            }
            //saving the first valid option as a prereq and the rest as alternatives
            //passes if a student has ANY of them
            for (Course req : orCourses) {
                savePrerequisite(course, req, Prerequisite.Type.PRE);
            }
            return;
        }

        //handling comma-separated prereqs
        if (prereqStr.contains(",")) {
            String[] parts = prereqStr.split(",");
            for (String part : parts) {
                String code = extractCourseCode(part.trim());
                if (code != null) {
                    Course req = courseRepository.findByCode(code);
                    if (req != null) savePrerequisite(course, req, Prerequisite.Type.PRE);
                }
            }
            return;
        }

        //handling single prereqs
        String code = extractCourseCode(prereqStr);
        if (code != null) {
            Course req = courseRepository.findByCode(code);
            if (req != null) savePrerequisite(course, req, Prerequisite.Type.PRE);
        }
    }

    private void savePrerequisite(Course course, Course required, Prerequisite.Type type) {
        //avoiding dupes
        if (prerequisiteRepository.findByCourse(course).stream().anyMatch(p -> p.getRequiredCourse().equals(required)
                        && p.getType() == type)) return;
        //otherwise saving
        Prerequisite prereq = new Prerequisite();
        prereq.setCourse(course);
        prereq.setRequiredCourse(required);
        prereq.setType(type);
        prerequisiteRepository.save(prereq);
    }

    private String extractCourseCode(String text) {
        //matching patterns of COMP-1000, MATH-1250 etc (letters - 4 numbers)
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("[A-Z]{2,4}-\\d{4}")
            .matcher(text.toUpperCase());
        return m.find() ? m.group() : null;
    }

    //parsing for the year level
    private int getYearLevel(String code) {
        java.util.regex.Matcher m = java.util.regex.Pattern
            .compile("\\d{4}")
            .matcher(code);
        if (m.find()) {
            int num = Integer.parseInt(m.group());
            if (num < 2000) return 1;
            if (num < 3000) return 2;
            if (num < 4000) return 3;
            return 4;
        }
        return 1;
    }

    //getting the category of the course, either MATH, STAT, or CORE
    private Course.Category getCategory(String code) {
        if (code.startsWith("MATH")) return Course.Category.MATH;
        if (code.startsWith("STAT")) return Course.Category.MATH;
        return Course.Category.CORE;
    }

    //Description map for all courses
    private java.util.Map<String, String> buildDescriptionMap() {
        java.util.Map<String, String> map = new java.util.HashMap<>();

        map.put("COMP-1000", "Introduces fundamental concepts in computer science including induction, recursion, algebraic characterization, syntax, semantics, formal logic, and complexity.");
        map.put("COMP-1047", "Introduction to computer system concepts including hardware, software, word processors, databases, spreadsheets, networking, and the Internet.");
        map.put("COMP-1400", "First of a two-course sequence introducing algorithm design and programming in C. Topics include variables, data types, sequential logic, decisions, loops, and modular programming.");
        map.put("COMP-1410", "Continuation of COMP-1400 covering multidimensional arrays, pointers, strings, advanced modular programming, recursion, stacks, and linked lists.");
        map.put("COMP-2057", "Introduces the Internet as a global information infrastructure including protocols, HTML, CSS, web design, security, and social networks.");
        map.put("COMP-2067", "Introduces fundamental computer programming principles and structured programming concepts with emphasis on good programming practices.");
        map.put("COMP-2077", "Introduces logic, critical appraisal, problem solving, and heuristics in the computer age including Boolean search and evaluation of web information.");
        map.put("COMP-2087", "Continuation of COMP-2067 introducing advanced algorithm design using Python including lists, dictionaries, functions, testing, and object-oriented programming.");
        map.put("COMP-2097", "Review, analysis and use of social media and mobile technologies including security, privacy, ethics, and social media analytics.");
        map.put("COMP-2120", "Covers classes and objects, Java applications, event handling, control structures, inheritance, polymorphism, exception handling, and introduction to GUI.");
        map.put("COMP-2140", "Covers grammars, recognizers, and translators for computer languages including regular languages, context-free languages, parsers, and compilers.");
        map.put("COMP-2310", "Introduction to mathematical logic, set theory, and graph theory including propositional logic, proof techniques, induction, and graph-theoretic concepts.");
        map.put("COMP-2540", "Introduction to linear and non-linear data structures including stacks, queues, trees, sorting and searching techniques, and algorithm design paradigms.");
        map.put("COMP-2547", "Introduction to linear and non-linear data structures and algorithms including stacks, queues, trees, sorting, searching, and algorithm design paradigms.");
        map.put("COMP-2560", "Advanced software development in C using UNIX. Topics include system calls, processes, signals, file processing, pipes, scripting, and network programming.");
        map.put("COMP-2650", "Covers digital design and CPU architecture including number systems, logic gates, circuit minimization, memory, sequential circuits, and CPU overview.");
        map.put("COMP-2660", "Uses microprocessor programming to explore CPU structure including assembly language, memory segmentation, instruction set architecture, and floating point.");
        map.put("COMP-2707", "Advanced website creation covering JavaScript, CSS, Dynamic HTML, XML, XHTML, and web browser compatibility issues.");
        map.put("COMP-2750", "Topics vary by year. Covers selected areas of computer science at the second-year level.");
        map.put("COMP-2800", "Advances programming skills and introduces software engineering concepts including event-driven programming, concurrent programming, and project management.");
        map.put("COMP-3037", "Covers practical information security topics including security policies, access controls, email security, database security, and wireless network security.");
        map.put("COMP-3057", "Examines ethics and societal change in the digital age covering free speech, intellectual property, privacy, security, and artificial intelligence ethics.");
        map.put("COMP-3067", "Covers relational database concepts including 3-level architecture, SQL, query formulation, and normalization.");
        map.put("COMP-3077", "Teaches design and building of interactive data-driven websites using PHP, MySQL, JSON, AJAX, jQuery, and web service APIs.");
        map.put("COMP-3110", "Introduces software engineering concepts including process models, requirements elicitation, rapid prototyping, design methodologies, and software evolution.");
        map.put("COMP-3150", "Covers database system concepts including file structures, relational model, relational algebra, SQL, and normalization theory.");
        map.put("COMP-3220", "Introduces object-oriented software analysis and design including cohesion, coupling, design patterns, and refactoring through case studies.");
        map.put("COMP-3250", "Covers data analysis and visualization of big data including statistics, regression, correlation analysis, and working with real datasets.");
        map.put("COMP-3300", "Covers operating system services including CPU scheduling, concurrent processes, process synchronization, deadlocks, memory management, and file systems.");
        map.put("COMP-3340", "Covers WWW information systems including authoring, dynamic documents, client-server model, multi-tier architecture, and security aspects.");
        map.put("COMP-3400", "Explores advanced object-oriented design using C++ including inheritance, templates, dynamic binding, exception handling, and multi-threading.");
        map.put("COMP-3500", "Covers multimedia system concepts including text, audio, video, media formats, data compression, hypermedia, and authoring tools.");
        map.put("COMP-3520", "Introduction to computer graphics including hardware, standards, programming libraries, fundamental algorithms, and 2D/3D rendering techniques.");
        map.put("COMP-3540", "Covers finite automata, regular expressions, context-free grammars, pushdown automata, Turing machines, and undecidability.");
        map.put("COMP-3670", "Introduction to computer networks including network architectures, transport, routing, data link protocols, addressing, and network security.");
        map.put("COMP-3680", "Covers practical network software and hardware including design, setup, configuration and implementation of network functions.");
        map.put("COMP-3710", "Covers fundamental AI concepts including search, propositional logic, knowledge representation, machine learning, and ethical implications.");
        map.put("COMP-3770", "Introduces professional game design and development tools including a commercial game engine, game physics, AI, 2D/3D graphics, and animation.");
        map.put("COMP-4110", "Covers software verification and testing concepts including the testing process, automated tools, and various test models with coverage criteria.");
        map.put("COMP-4150", "Covers advanced database theory and application development tools completing database design from COMP-3150 and adding development languages.");
        map.put("COMP-4200", "Teaches Android mobile application development including UI design, data management, remote server interfacing, and deployment.");
        map.put("COMP-4220", "Project-oriented course covering Agile software development principles, extreme programming, UI design, data persistence, and industry tools.");
        map.put("COMP-4250", "Introduces data mining and big data analytics including NoSQL databases, MapReduce, Hadoop, frequent itemset mining, and clustering.");
        map.put("COMP-4400", "Covers programming language concepts including imperative, object-oriented, functional, logic, and concurrent programming paradigms.");
        map.put("COMP-4500", "Covers multimedia application development including 3D modeling, animation, standalone and networked multimedia systems.");
        map.put("COMP-4540", "Introduces algorithm design and analysis including asymptotic bounds, advanced data structures, graph algorithms, and NP completeness.");
        map.put("COMP-4670", "Covers advanced network security topics including encryption, authentication, intrusion detection, and security of email and web access.");
        map.put("COMP-4680", "Introduces advanced networking topics for computer science students building on knowledge from COMP-3670 and COMP-3680.");
        map.put("COMP-4730", "Introduces advanced topics in Artificial Intelligence for honours computer science students.");
        map.put("COMP-4740", "Continues advanced topics in Artificial Intelligence for honours computer science students.");
        map.put("COMP-4770", "Explores AI for games including agents, steering behaviours, pathfinding, decision making, planning, and multi-agent systems.");
        map.put("COMP-4800", "Connects emerging technologies with software engineering concepts including protocol security, web systems, and distributed object systems.");
        map.put("COMP-4960", "Research project course requiring students to complete a research project in computer science under faculty supervision with a written report.");
        map.put("COMP-4990", "Project management course requiring students to complete an application development project under faculty supervision with presentations.");
        map.put("MATH-1020", "Covers mathematical foundations including discrete mathematics topics required for computer science.");
        map.put("MATH-1250", "Covers linear algebra concepts including vectors, matrices, systems of linear equations, and vector spaces.");
        map.put("MATH-1260", "Covers vectors and linear algebra for students with Ontario Grade 12 Advanced Functions.");
        map.put("MATH-1270", "Linear algebra course designed for engineering students covering matrices and vector spaces.");
        map.put("MATH-1280", "Access course providing foundation for linear algebra studies.");
        map.put("MATH-1720", "Covers differential calculus including limits, derivatives, and applications.");
        map.put("MATH-1760", "Covers functions and differential calculus including limits, continuity, and derivatives.");
        map.put("MATH-1730", "Covers integral calculus including antiderivatives, definite integrals, and applications.");
        map.put("MATH-3940", "Covers numerical analysis methods for computer scientists including approximation, interpolation, and numerical integration.");
        map.put("STAT-2910", "Introduction to statistics for science students covering descriptive statistics, probability, distributions, and hypothesis testing.");
        map.put("STAT-2920", "Introduction to probability theory covering random variables, distributions, expectation, and limit theorems.");

        return map;
    }
}