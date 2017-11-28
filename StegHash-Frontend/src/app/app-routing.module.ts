import {NgModule}             from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

import {LoginComponent}       from "./components/login/login.component";
import {HomeComponent}       from "./components/home/home.component";
import {SettingsComponent}       from "./components/settings/settings.component";
import {SentComponent} from "./components/sent/sent.component";
import {CreateComponent} from "./components/create/create.component";


const routes: Routes = [
  {path: '', redirectTo: '/home', pathMatch: 'full'},
  {path: 'login', component: LoginComponent},
  {path: 'home', component: HomeComponent},
  {path: 'settings', component: SettingsComponent},
  {path: 'sent', component: SentComponent},
  {path: 'create', component: CreateComponent},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
