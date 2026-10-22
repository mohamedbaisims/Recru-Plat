import { Component, OnInit, ElementRef, ViewChild } from '@angular/core';
import { ApplicationFormService } from '../../serives/application-form.service';
import { ApplicationForm } from '../../models/application-form';

@Component({
  selector: 'app-application-form',
  templateUrl: './application-form.component.html',
  styleUrls: ['./application-form.component.scss']
})
export class ApplicationFormComponent implements OnInit {
  /** Current wizard step: 1 = CV upload + OCR, 2 = review fields + docs, 3 = submit */
  step = 1;
  userId!: number;

  // ── Files ────────────────────────────────────────────────────────────────────
  cvFile: File | null = null;
  motivationLetterFile: File | null = null;
  diplomaFile: File | null = null;

  // ── OCR state ────────────────────────────────────────────────────────────────
  isAnalyzing = false;
  ocrDone = false;
  ocrError = '';

  // ── Submit state ─────────────────────────────────────────────────────────────
  isSubmitting = false;
  submitError = '';
  submitSuccess = '';

  // ── Form data ────────────────────────────────────────────────────────────────
  form: ApplicationForm = {
    fullName: '', address: '', phoneNumber: '', email: '', birthDate: '',
    birthPlace: '', nationality: '', diploma: '', diplomaYear: '', institution: '',
    experience: '', technicalSkills: '', personalSkills: '',
    cvPath: '', motivationLetterPath: '', diplomaFilePath: '',
    user: { id: 0 }
  };

  nationalities: string[] = [
    'Tunisienne', 'Française', 'Algérienne', 'Marocaine', 'Canadienne', 'Belge',
    'Italienne', 'Espagnole', 'Allemande', 'Américaine', 'Libanaise', 'Égyptienne'
  ];

  diplomas: string[] = [
    'Baccalauréat', 'BTS', 'Licence', 'Maîtrise', 'Master', 'Mastère professionnel',
    'Mastère recherche', 'Doctorat'
  ];

  diplomaYears: string[] = [];

  @ViewChild('cvInput') cvInputRef!: ElementRef<HTMLInputElement>;
  @ViewChild('motivationInput') motivationInputRef!: ElementRef<HTMLInputElement>;
  @ViewChild('diplomaInput') diplomaInputRef!: ElementRef<HTMLInputElement>;

  constructor(private appService: ApplicationFormService) {}

  ngOnInit(): void {
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload: any = JSON.parse(atob(token.split('.')[1]));
        this.userId = payload.id;
        this.form.fullName = `${payload.firstname} ${payload.lastname}`;
        this.form.email = payload.sub;
      } catch (e) { /* ignore */ }
    }
    const currentYear = new Date().getFullYear();
    for (let y = currentYear; y >= 1980; y--) {
      this.diplomaYears.push(y.toString());
    }
  }

  // ── Step 1: CV selection ──────────────────────────────────────────────────────
  triggerCvInput() {
    this.cvInputRef.nativeElement.click();
  }

  onCvSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input?.files?.length) return;
    const file = input.files[0];
    if (!this.isPdf(file)) {
      this.ocrError = 'Veuillez sélectionner un fichier PDF.';
      input.value = '';
      return;
    }
    this.cvFile = file;
    this.ocrDone = false;
    this.ocrError = '';
  }

  analyzeCV() {
    if (!this.cvFile) return;
    this.isAnalyzing = true;
    this.ocrError = '';

    const fd = new FormData();
    fd.append('cv', this.cvFile);

    this.appService.extractCv(fd).subscribe({
      next: (data: any) => {
        // Fill form fields with OCR result (keep existing fullName/email from token)
        if (data.fullName)      this.form.fullName      = data.fullName;
        if (data.email)         this.form.email         = data.email;
        if (data.phoneNumber)   this.form.phoneNumber   = data.phoneNumber;
        if (data.address)       this.form.address       = data.address;
        if (data.birthDate)     this.form.birthDate     = data.birthDate;
        if (data.birthPlace)    this.form.birthPlace    = data.birthPlace;
        if (data.nationality)   this.form.nationality   = data.nationality;
        if (data.diploma)       this.form.diploma       = data.diploma;
        if (data.diplomaYear)   this.form.diplomaYear   = data.diplomaYear;
        if (data.institution)   this.form.institution   = data.institution;
        if (data.experience)    this.form.experience    = data.experience;
        if (data.technicalSkills) this.form.technicalSkills = data.technicalSkills;
        if (data.personalSkills)  this.form.personalSkills  = data.personalSkills;

        this.isAnalyzing = false;
        this.ocrDone = true;
        // Auto-advance to step 2 after a brief moment
        setTimeout(() => this.step = 2, 800);
      },
      error: (err: any) => {
        console.error('OCR error', err);
        this.ocrError = 'Erreur lors de l\'analyse du CV. Vérifiez le fichier ou remplissez manuellement.';
        this.isAnalyzing = false;
        // Still allow manual entry
        this.ocrDone = true;
        setTimeout(() => this.step = 2, 1200);
      }
    });
  }

  // ── Step 2: other file selections ────────────────────────────────────────────
  triggerMotivationInput() { this.motivationInputRef.nativeElement.click(); }
  triggerDiplomaInput()    { this.diplomaInputRef.nativeElement.click(); }

  onMotivationSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input?.files?.length) return;
    const file = input.files[0];
    if (!this.isPdf(file)) { this.submitError = 'Fichier PDF requis.'; input.value = ''; return; }
    this.motivationLetterFile = file;
    this.submitError = '';
  }

  onDiplomaSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (!input?.files?.length) return;
    const file = input.files[0];
    if (!this.isPdf(file)) { this.submitError = 'Fichier PDF requis.'; input.value = ''; return; }
    this.diplomaFile = file;
    this.submitError = '';
  }

  // ── Validation ────────────────────────────────────────────────────────────────
  canProceedToStep3(): boolean {
    return !!this.motivationLetterFile && !!this.diplomaFile &&
      !!this.form.fullName?.trim() && !!this.form.email?.trim();
  }

  canSubmit(): boolean {
    return !!this.cvFile &&
      !!this.motivationLetterFile &&
      !!this.diplomaFile &&
      !!this.form.fullName?.trim() &&
      !!this.form.email?.trim();
  }

  nextStep() { if (this.step < 3) this.step++; }
  prevStep() { if (this.step > 1) this.step--; }

  // ── Final submit ──────────────────────────────────────────────────────────────
  submit() {
    this.submitError = '';
    this.submitSuccess = '';

    const token = localStorage.getItem('token');
    if (!token) { this.submitError = 'Session expirée. Veuillez vous reconnecter.'; return; }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      if (!payload?.id) { this.submitError = "Impossible d'extraire l'identifiant utilisateur."; return; }
      this.userId = payload.id;
      this.form.user = { id: this.userId };
    } catch (e) {
      this.submitError = 'Token invalide. Veuillez vous reconnecter.'; return;
    }

    if (!this.canSubmit()) {
      this.submitError = 'Veuillez remplir tous les champs obligatoires et ajouter les fichiers PDF.';
      return;
    }

    const formData = new FormData();
    formData.append('fullName',       this.form.fullName       || '');
    formData.append('address',        this.form.address        || '');
    formData.append('phoneNumber',    this.form.phoneNumber    || '');
    formData.append('email',          this.form.email          || '');
    formData.append('birthDate',      this.form.birthDate      || '');
    formData.append('birthPlace',     this.form.birthPlace     || '');
    formData.append('nationality',    this.form.nationality    || '');
    formData.append('diploma',        this.form.diploma        || '');
    formData.append('diplomaYear',    this.form.diplomaYear    || '');
    formData.append('institution',    this.form.institution    || '');
    formData.append('experience',     this.form.experience     || '');
    formData.append('technicalSkills',this.form.technicalSkills|| '');
    formData.append('personalSkills', this.form.personalSkills || '');
    formData.append('userId',         this.userId.toString());
    formData.append('cv',             this.cvFile as File);
    formData.append('motivationLetter', this.motivationLetterFile as File);
    formData.append('diplomaFile',    this.diplomaFile as File);

    this.isSubmitting = true;
    this.appService.createFormWithFiles(formData).subscribe({
      next: () => {
        this.submitSuccess = 'Candidature envoyée avec succès !';
        this.isSubmitting = false;
      },
      error: (err: any) => {
        this.submitError = err?.error?.message || 'Erreur lors de l\'enregistrement de la candidature.';
        this.isSubmitting = false;
      }
    });
  }

  private isPdf(file: File): boolean {
    return file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf');
  }
}
