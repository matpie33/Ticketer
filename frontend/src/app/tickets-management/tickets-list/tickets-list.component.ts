import {Component, OnInit} from '@angular/core';
import {RestHandlerService} from "../../rest-handler.service";
import {UserTicket} from "../data/ticket-of-user";
import {LoginConstants} from "../../login/login-enums";
import {DATE_FORMAT} from "../../constants";

@Component({
  selector: 'app-tickets-list',
  templateUrl: './tickets-list.component.html',
  styleUrls: ['./tickets-list.component.css']
})
export class TicketsListComponent implements OnInit{
 tickets: Array<UserTicket> = [];
  dateFormat = DATE_FORMAT;

 constructor(private restHandler: RestHandlerService) {
 }

  ngOnInit(): void {
    this.restHandler.getTicketsOfUser(Number.parseInt(sessionStorage.getItem(LoginConstants.USER_ID)!)).subscribe(tickets => {
      this.tickets = tickets;
    })
  }
}
