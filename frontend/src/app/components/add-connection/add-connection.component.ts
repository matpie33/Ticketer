import { Component } from '@angular/core';
import {FormBuilder, FormGroup} from "@angular/forms";
import {RestHandlerService} from "../../services/rest-handler.service";
import {ViewWithStatus} from "../common/view-with-status";

@Component({
  selector: 'app-add-connection',
  templateUrl: './add-connection.component.html',
  styleUrls: ['./add-connection.component.css']
})
export class AddConnectionComponent extends ViewWithStatus{
  form: FormGroup = this.formBuilder.group({
    fromStation: "",
    toStation: "",
    time: "11:11",
    train: ""
  });

  trains: Map<number, String> = new Map<number, String>();

  SUCCESS_MESSAGE = "Successfully added travel connection.";
  ERROR_MESSAGE = "Error while adding travel connection.";
  timeoutId: number = 0;

  constructor(private formBuilder: FormBuilder, private restHandlerService: RestHandlerService) {
    super();
  }

  ngOnInit (){
    this.restHandlerService.getTrains().subscribe(trains=>{
      for (let train of trains){
        this.trains.set(train.id, train.name);
      }
      this.form.controls["train"].setValue(1);
    });

  }

  onSubmit (){
    this.restHandlerService.addConnection(this.form.controls["fromStation"].value,
      this.form.controls["toStation"].value, this.form.controls["time"].value, this.form.controls["train"].value ).subscribe({
        next: this.handleSuccess.bind(this),
        error: this.handleError.bind(this)
    });
  }



  handleSuccess(){
    this.showSuccessMessage(this.SUCCESS_MESSAGE);
    this.timeoutId = this.hideStatusAfterDelay();
  }

  handleError (){
    this.showErrorMessage(this.ERROR_MESSAGE);
    clearTimeout(this.timeoutId);
  }

}
