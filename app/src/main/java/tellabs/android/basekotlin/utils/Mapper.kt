package tellabs.android.basekotlin.utils

import tellabs.android.basekotlin.data.remote.response.TeamModel
import tellabs.android.basekotlin.domain.Team

fun mapToDomain(team: TeamModel) : Team {
    return Team(
        teamId = team.idTeam,
        teamName = team.strTeam,
        teamDescription = team.strDescriptionEN,
        teamLogo = team.strTeamBadge,
        teamStadiumName = team.strStadium
    )
}

fun mapToListDomain(teams: List<TeamModel>) : List<Team> {
    val listDomain = mutableListOf<Team>()
    teams.map { listDomain.add(mapToDomain(it)) }
    return listDomain
}