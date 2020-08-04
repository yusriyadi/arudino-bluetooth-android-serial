package tellabs.android.basekotlin.data.repository

import tellabs.android.basekotlin.data.db.dao.TeamDao
import tellabs.android.basekotlin.data.remote.response.TeamResponse
import tellabs.android.basekotlin.data.remote.service.TeamService
import tellabs.android.basekotlin.domain.Team
import tellabs.android.basekotlin.utils.mapToListDomain

class TeamRepositoryImpl(val teamService: TeamService,
                         val teamDao: TeamDao) : TeamRepository{

    override suspend fun getTeams(league: String): List<Team> {
        return mapToListDomain(teamService.getAllTeams(league).teams)
    }

    //the way without maping
    override suspend fun getTeamsNonMap(league: String): TeamResponse {
        return teamService.getAllTeams(league)
    }


}