export interface ApplicationForm {
  id?: number;
  fullName: string;
  address: string;
  phoneNumber: string;
  email: string;
  birthDate: string;
  birthPlace: string;
  nationality: string;
  institution: string;
  diploma: string;
  diplomaYear: string;
  technicalSkills: string;
  personalSkills: string;
  cvPath?: string;
  experience: string;
  diplomaFilePath?: string;
  motivationLetterPath?: string;
  user?: { id: number }; // si tu envoies aussi l'user
}
