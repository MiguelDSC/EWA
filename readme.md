<div align="center">

# Climate Cleanup

*By team CCU-1*
</div>

## Install
The installation of this application is divided in two parts: [client](client/README.md) & [server](server/readme.md) side. These markdown files will specify how to run the client and server.


## Class responsibilities ( server/src/main/java/app )
Classes per person (some may overlap).

* B. Salfischberger < bart.salfischberger@hva.nl >
    * models.greenhouse.Greenhouse.java
    * models.greenhouse.GreenhouseMeasurement.java
    * models.greenhouse.GreenhouseSetting.java
    * repositories.greenhouse.GreenhouseData.java
    * repositories.greenhouse.GreenhouseDataJpa.java
    * repositories.greenhouse.GreenhouseDataMock.java
    * rest.authentication.LoginController.java
    * rest.authentication.TeamselectController.java
    * rest.greenhouse.GreenhouseController.java
    * rest.greenhouse.GreenhouseHandler.java
    * rest.security.JWTokenInfo.java
    * rest.security.JWTokenUtil.java
    * rest.security.JWTRequestFilter.java
    * rest.security.Password.java
    * rest.Cors.java
    * rest.Schedular.java
    * rest.WebSocket.java

* R. Siepelinga < rick.siepelinga@hva.nl, rick-siepelinga@hotmail.com >
    * models.invite.Invite.java
    * models.user.Role.java
    * models.user.Team.java
    * models.user.User.java
    * models.UserTeam.java
    * repositories.invite.InviteInterface.java
    * repositories.invite.InviteRepository.java
    * repositories.role.RoleRepository.java
    * repositories.team.TeamRepositoryJPA.java
    * repositories.user.UserRepository.java
    * repositories.user.UserRepositoryJPA.java
    * repositories.user.UserRepositoryMock.java
    * repositories.userteam.UserTeamRepository.java
    * repositories.user.UserTeamRepositoryJPA.java
    * rest.authentication.RegisterController.java
    * rest.role.RoleController.java
    * rest.team.TeamController.java
    * rest.userTeam.userTeamController.java
    * rest.UMSController.java

* M. da Silva Crespim < miguel.da.silva.crespim@hva.nl >
    * models.note.Note.java
    * models.user.NoteCategory.java
    * repositories.note.NoteInterface.java
    * repositories.note.NoteRepository.java
    * repositories.userteam.UserTeamRepository.java
    * repositories.user.UserTeamRepositoryJPA.java
    * rest.note.NoteController.java
    * rest.userteam.userTeamController.java

* S. de Jong < silas.de.jong@hva.nl >
    * models.SettingsAPIResponse
    * models.Settings
    * models.Image
    * repositories.Settings.SettingsInterface
    * repositories.Settings.SettingsRepository
    * rest.settings.settings

* S.Kan < stef.kan@hva.nl >
    * rest.security.JWTokenInfo.java
    * rest.security.JWTokenUtil.java
    * rest.security.JWTRequestFilter.java
    * rest.team.TeamController.java
    * repositories.team.Teamrepository.java
    * repositories.team.TeamRepositoryJPA.java
    * repositories.greenhouse.GreenhouseRepository.java
    * repositories.greenhouse.GreenhouseRepositoryJpa.java

## Class responsibilities ( client/src/app )
Excluding interfaces.

* B. Salfischberger < bart.salfischberger@hva.nl >
  * components/data-page/*
  * components/navbar/*
  * components/team-switch/*
  * models/data/chart.ts
  * services/authentication/login.ts
  * services/greenhouse-data-service/*
  * services/greenhouse-websocket/*
  * services/team-switch/*

* R. Siepelinga < rick.siepelinga@hva.nl, rick-siepelinga@hotmail.com >
  * components/authentication/register/*
  * components/team/*
  * models/team.ts
  * models/user.model.ts
  * models/userTeam.ts
  * services/member/*
  
* S. de Jong < silas.de.jong@hva.nl >
  * components/authentication/login/login.component.ts
  * components/settings/*
  * Validators.ts

* M. da Silva Crespim < miguel.da.silva.crespim@hva.nl >
  * components/note-page
  * models/note/note.ts
  * components/authentication/register/authenticate.component.ts
  * service/note



## Tests
Tests made per person.

* B. Salfischberger < bart.salfischberger@hva.nl >
    * Back end
        * rest.authentication.LoginController.java (1 test)
        * rest.greenhouse.GreenhouseController.java (4 tests)
    * Front end
        * components/data-page/data-page-edit/data-page.component.spec.ts (5 tests)

* R. Siepelinga < rick.siepelinga@hva.nl >
    * rest.UMSController.java (2 tests)
    * repositories.user.UserRepository.java (3 tests)
    > Front end tests made by R. Siepelinga
    * (client.src.app.service.member).user.service.spec.ts (5 tests)
    * (client.src.app.components.team).team.component.spec.ts (2 tests)
    
* M. da Silva Crespim < miguel.da.silva.crespim@hva.nl >
    > Back end tests 
    * rest.note.NoteController.java (2 tests)
    * repositories.note.NoteRepository.java (4 tests)
    > Front end tests 
    * (client.src.app.service.note).note.service.spec.ts (2 tests)
    * (client.src.app.components.note-page.note-overview).note-overview.component.spec.ts (2 tests)
    * (client.src.app.components.authentication.login).login.component.spec.ts (2 tests)

* S. de Jong < silas.de.jong@hva.nl >
    > Backend tests
    * test.java.app.Settings.controller.TestSettingsController ( 2 tests )
    * test.java.app.Settings.repository.TestSettingsRepository ( 3 tests )
    > Frontend tests
    * components/settings/settings.component.spec.ts ( 5 tests )

* S.Kan < stef.kan@hva.nl >
    > Backend Tests
    * test.java.app.models.rest.team.TeamController (6 tests)
    > Frontend tests
    * app.components.admin.admin-management.admin-management-spec.ts (3 tests)
    * app.service.team.team-service.spec.ts (4 tests)
