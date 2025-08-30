package app.titech.moodle

import app.titech.moodle.request.AssignmentSubmissionStatusRequest
import app.titech.moodle.request.AssignmentSubmissionStatusResponse
import app.titech.moodle.request.AssignmentsRequest
import app.titech.moodle.request.AssignmentsResponse
import app.titech.moodle.request.CourseCategoriesResponse
import app.titech.moodle.request.CourseCategoryRequest
import app.titech.moodle.request.CourseContentsRequest
import app.titech.moodle.request.CourseContentsResponse
import app.titech.moodle.request.SiteInfoRequest
import app.titech.moodle.request.SiteInfoResponse
import app.titech.moodle.request.UserEnrolCourseRequest
import app.titech.moodle.request.UserEnrolCoursesResponse
import java.net.URL

class MoodleClient(
    val baseURL: URL,
    val userAgent: String,
    val apiClient: APIClient = APIClientImpl(
        baseURL = baseURL,
        userAgent = userAgent
    )
) {
    suspend fun getSiteInfo(wsToken: String): SiteInfoResponse =
        apiClient.send(SiteInfoRequest(wsToken))

    suspend fun getUserCourses(userId: Int, wsToken: String): UserEnrolCoursesResponse =
        apiClient.send(UserEnrolCourseRequest(userId, wsToken))

    suspend fun getCourseCategories(wsToken: String): CourseCategoriesResponse =
        apiClient.send(CourseCategoryRequest(wsToken))

    suspend fun getCourseContents(courseId: Int, wsToken: String): CourseContentsResponse =
        apiClient.send(CourseContentsRequest(courseId, wsToken))

    suspend fun getAssignments(wsToken: String): AssignmentsResponse =
        apiClient.send(AssignmentsRequest(wsToken))

    suspend fun getAssignmentSubmissionStatus(
        assignmentId: Int,
        userId: Int,
        wsToken: String
    ): AssignmentSubmissionStatusResponse =
        apiClient.send(AssignmentSubmissionStatusRequest(assignmentId, userId, wsToken))
}


