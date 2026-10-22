export interface ApplicationFormDTO {
  id: number;
  fullName: string;
  email: string;
  diploma: string;
  address: string;
  institution: string;
  userEmail: string;
  userFullName: string;
  experience: string;
    technicalSkills: string;
    personalSkills: string;
   cvPath?: number[];                // fichier PDF du CV encodé
    diplomaFilePath?: number[];      // fichier PDF du diplôme encodé
    motivationLetterPath?: number[]; // fichier PDF de la lettre de motivation
}
