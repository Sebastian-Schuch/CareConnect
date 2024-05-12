import {TreatmentMedicineDtoCreate, TreatmentMedicineSelection} from "./treatmentMedicine";
import {UserDetailDto} from "./user";
import {OutpatientDepartmentDto} from "./outpatient-department";

export interface TreatmentDtoCreate {
  treatmentTitle: string;
  treatmentStart: Date;
  treatmentEnd: Date;
  patient: UserDetailDto;
  outpatientDepartment: OutpatientDepartmentDto;
  treatmentText?: string;
  doctors: UserDetailDto[];
  medicines?: TreatmentMedicineDtoCreate[];
}

export interface TreatmentDto {
  id: number;
  treatmentTitle: string;
  treatmentStart: Date;
  treatmentEnd: Date;
  patient: UserDetailDto;
  outpatientDepartment: OutpatientDepartmentDto;
  treatmentText?: string;
  doctors: UserDetailDto[];
  medicines?: TreatmentMedicineSelection[];
}
