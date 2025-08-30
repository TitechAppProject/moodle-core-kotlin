package app.titech

import app.titech.moodle.APIClientImpl
import app.titech.moodle.APIClientMock
import app.titech.moodle.MoodleClient
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.net.URL
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class MoodleClientTest {
    @Test
    fun testGetSiteInfo() = runBlocking {
        val jsonResponse = """
            {
                "userid": 123,
                "username": "testuser",
                "fullname": "Test User",
                "firstname": "Test",
                "lastname": "User"
            }
        """.trimIndent()
        
        val moodleClient = MoodleClient(
            apiClient = APIClientMock(jsonResponse),
            baseURL = URL("https://moodle.example.com"),
            userAgent = "MoodleClientTest"
        )

        val siteInfo = moodleClient.getSiteInfo("mocked_token")
        
        assertEquals(123, siteInfo.userid)
        assertEquals("testuser", siteInfo.username)
        assertEquals("Test User", siteInfo.fullname)
        assertEquals("Test", siteInfo.firstname)
        assertEquals("User", siteInfo.lastname)
    }

    @Test
    fun testGetUserCourses() = runBlocking {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "shortname": "CS101",
                    "fullname": "Computer Science 101",
                    "category": 5,
                    "startdate": 1630454400,
                    "enddate": 1640995200
                },
                {
                    "id": 2,
                    "shortname": "MATH201",
                    "fullname": "Advanced Mathematics",
                    "category": 3,
                    "startdate": 1630454400,
                    "enddate": 1640995200
                }
            ]
        """.trimIndent()
        
        val moodleClient = MoodleClient(
            apiClient = APIClientMock(jsonResponse),
            baseURL = URL("https://moodle.example.com"),
            userAgent = "MoodleClientTest"
        )

        val courses = moodleClient.getUserCourses(123, "mocked_token")
        
        assertEquals(2, courses.size)
        
        assertEquals(1, courses[0].id)
        assertEquals("CS101", courses[0].shortname)
        assertEquals("Computer Science 101", courses[0].fullname)
        assertEquals(5, courses[0].category)
        assertEquals(1630454400, courses[0].startdate)
        assertEquals(1640995200, courses[0].enddate)
        
        assertEquals(2, courses[1].id)
        assertEquals("MATH201", courses[1].shortname)
        assertEquals("Advanced Mathematics", courses[1].fullname)
        assertEquals(3, courses[1].category)
        assertEquals(1630454400, courses[1].startdate)
        assertEquals(1640995200, courses[1].enddate)
    }

    @Test
    fun testGetCourseCategories() = runBlocking {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "name": "Engineering",
                    "parent": 0,
                    "depth": 1,
                    "path": "/1"
                },
                {
                    "id": 2,
                    "name": "Computer Science",
                    "parent": 1,
                    "depth": 2,
                    "path": "/1/2"
                }
            ]
        """.trimIndent()
        
        val moodleClient = MoodleClient(
            apiClient = APIClientMock(jsonResponse),
            baseURL = URL("https://moodle.example.com"),
            userAgent = "MoodleClientTest"
        )

        val categories = moodleClient.getCourseCategories("mocked_token")
        
        assertEquals(2, categories.size)
        
        assertEquals(1, categories[0].id)
        assertEquals("Engineering", categories[0].name)
        assertEquals(0, categories[0].parent)
        assertEquals(1, categories[0].depth)
        assertEquals("/1", categories[0].path)
        
        assertEquals(2, categories[1].id)
        assertEquals("Computer Science", categories[1].name)
        assertEquals(1, categories[1].parent)
        assertEquals(2, categories[1].depth)
        assertEquals("/1/2", categories[1].path)
    }

    @Test
    fun testGetCourseContents() = runBlocking {
        val jsonResponse = """
            [
                {
                    "id": 1,
                    "name": "Week 1",
                    "summary": "Introduction to the course",
                    "modules": [
                        {
                            "id": 101,
                            "modname": "resource",
                            "url": "https://moodle.example.com/mod/resource/view.php?id=101",
                            "name": "Course Syllabus",
                            "description": "Download the course syllabus",
                            "modicon": "https://moodle.example.com/theme/image.php/boost/core/1234567890/f/pdf",
                            "modplural": "Resources",
                            "completion": 1,
                            "completiondata": {
                                "stateval": 0
                            },
                            "contents": [
                                {
                                    "type": "file",
                                    "filename": "syllabus.pdf",
                                    "filepath": "/",
                                    "filesize": 204800,
                                    "fileurl": "https://moodle.example.com/webservice/pluginfile.php/123/mod_resource/content/1/syllabus.pdf",
                                    "mimetype": "application/pdf",
                                    "timecreated": 1630454400,
                                    "timemodified": 1630454400,
                                    "sortorder": 1,
                                    "userid": 2,
                                    "author": "Professor Smith",
                                    "licenseval": "cc"
                                }
                            ]
                        }
                    ]
                }
            ]
        """.trimIndent()
        
        val moodleClient = MoodleClient(
            apiClient = APIClientMock(jsonResponse),
            baseURL = URL("https://moodle.example.com"),
            userAgent = "MoodleClientTest"
        )

        val contents = moodleClient.getCourseContents(1, "mocked_token")
        
        assertEquals(1, contents.size)
        assertEquals(1, contents[0].id)
        assertEquals("Week 1", contents[0].name)
        assertEquals("Introduction to the course", contents[0].summary)
        
        val modules = contents[0].modules
        assertEquals(1, modules.size)
        assertEquals(101, modules[0].id)
        assertEquals("resource", modules[0].modname)
        assertEquals("Course Syllabus", modules[0].name)
        assertEquals(1, modules[0].completion)
        
        val moduleContents = modules[0].contents
        assertNotNull(moduleContents)
        assertEquals(1, moduleContents.size)
        assertEquals("syllabus.pdf", moduleContents[0].filename)
        assertEquals(204800L, moduleContents[0].filesize)
    }

    @Test
    fun testGetAssignments() = runBlocking {
        val jsonResponse = """
            {
                "courses": [
                    {
                        "id": 1,
                        "fullname": "Computer Science 101",
                        "shortname": "CS101",
                        "timemodified": 1630454400,
                        "assignments": [
                            {
                                "id": 1,
                                "cmid": 10,
                                "course": 1,
                                "name": "Assignment 1",
                                "nosubmissions": 0,
                                "submissiondrafts": 0,
                                "sendnotifications": 1,
                                "sendlatenotifications": 1,
                                "sendstudentnotifications": 1,
                                "duedate": 1640995200,
                                "allowsubmissionsfromdate": 1630454400,
                                "grade": 100,
                                "timemodified": 1630454400,
                                "completionsubmit": 1,
                                "cutoffdate": 1641081600,
                                "teamsubmission": 0,
                                "requireallteammemberssubmit": 0,
                                "teamsubmissiongroupingid": 0,
                                "blindmarking": 0,
                                "revealidentities": 0,
                                "attemptreopenmethod": "none",
                                "maxattempts": 1,
                                "markingworkflow": 0,
                                "markingallocation": 0,
                                "requiresubmissionstatement": 0
                            }
                        ]
                    }
                ]
            }
        """.trimIndent()
        
        val moodleClient = MoodleClient(
            apiClient = APIClientMock(jsonResponse),
            baseURL = URL("https://moodle.example.com"),
            userAgent = "MoodleClientTest"
        )

        val assignmentsResponse = moodleClient.getAssignments("mocked_token")
        
        assertEquals(1, assignmentsResponse.courses.size)
        
        val course = assignmentsResponse.courses[0]
        assertEquals(1, course.id)
        assertEquals("Computer Science 101", course.fullname)
        assertEquals("CS101", course.shortname)
        
        val assignments = course.assignments
        assertEquals(1, assignments.size)
        assertEquals(1, assignments[0].id)
        assertEquals("Assignment 1", assignments[0].name)
        assertEquals(100, assignments[0].grade)
        assertEquals(1640995200, assignments[0].duedate)
    }

    @Test
    fun testGetAssignmentSubmissionStatus() = runBlocking {
        val jsonResponse = """
            {
                "lastattempt": {
                    "submission": {
                        "id": 1,
                        "userid": 123,
                        "attemptnumber": 1,
                        "timecreated": 1630454400,
                        "timemodified": 1630540800,
                        "status": "submitted"
                    }
                },
                "feedback": {
                    "grade": {
                        "id": 1,
                        "userid": 123,
                        "attemptnumber": 1,
                        "timecreated": 1630627200,
                        "timemodified": 1630627200,
                        "grader": 2,
                        "grade": "85.00",
                        "gradefordisplay": "85.00 / 100.00"
                    },
                    "gradefordisplay": "85.00 / 100.00",
                    "gradeddate": 1630627200
                },
                "previousattempts": []
            }
        """.trimIndent()
        
        val moodleClient = MoodleClient(
            apiClient = APIClientMock(jsonResponse),
            baseURL = URL("https://moodle.example.com"),
            userAgent = "MoodleClientTest"
        )

        val status = moodleClient.getAssignmentSubmissionStatus(1, 123, "mocked_token")
        
        val lastAttempt = status.lastattempt
        assertNotNull(lastAttempt)
        assertNotNull(lastAttempt.submission)
        assertEquals(1, lastAttempt.submission?.id)
        assertEquals(123, lastAttempt.submission?.userid)
        assertEquals("submitted", lastAttempt.submission?.status)
        
        val feedback = status.feedback
        assertNotNull(feedback)
        assertNotNull(feedback.grade)
        assertEquals("85.00", feedback.grade?.grade)
        assertEquals("85.00 / 100.00", feedback.gradefordisplay)
    }
}