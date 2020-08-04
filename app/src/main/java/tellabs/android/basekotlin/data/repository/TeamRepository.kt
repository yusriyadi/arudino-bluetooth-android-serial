package tellabs.android.basekotlin.data.repository

import tellabs.android.basekotlin.data.remote.response.TeamResponse
import tellabs.android.basekotlin.domain.Team

interface TeamRepository {

    suspend fun getTeams(league: String) : List<Team>

    //use this code if you need a whole response
    suspend fun getTeamsNonMap(league: String): TeamResponse
}