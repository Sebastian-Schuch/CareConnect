import {MedicationDto} from "./medication";

export interface TreatmentMedicineDtoCreate {
  medication: MedicationDto;
  amount: number;
  unitOfMeasurement: string;
  medicineAdministrationDate: Date;
}

export interface TreatmentMedicineSelection {
  id: number;
  medication: MedicationDto;
  amount: number;
  unitOfMeasurement: string;
  medicineAdministrationDate: Date;
}
