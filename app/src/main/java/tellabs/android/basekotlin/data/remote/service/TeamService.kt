package tellabs.android.basekotlin.data.remote.service

import retrofit2.http.GET
import retrofit2.http.Query
import tellabs.android.basekotlin.data.remote.response.TeamResponse

interface TeamService {

    @GET("search_all_teams.php")
   suspend  fun getAllTeams(@Query("l") league: String) : TeamResponse



}