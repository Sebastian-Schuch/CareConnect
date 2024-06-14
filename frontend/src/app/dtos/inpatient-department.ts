export interface InpatientDepartmentDtoCreate {
  name: string;
  capacity: number;
}

export interface InpatientDepartmentDto {
  id: number;
  name: string;
  capacity: number;
}

export interface InpatientDepartmentPageDto {
  inpatientDepartments: InpatientDepartmentDto[];
  totalItems: number;
}
