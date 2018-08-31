(custom-set-variables
 ;; custom-set-variables was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 '(column-number-mode t)
 '(custom-enabled-themes (quote (tango-dark)))
 '(inhibit-startup-screen t)
 '(show-paren-mode t))
(custom-set-faces
 ;; custom-set-faces was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 )

;; keyboard for emacs running in OS X
(defvar system-type-as-string (prin1-to-string system-type))
(defvar on_darwin     (string-match "darwin" system-type-as-string))
(cond (on_darwin
       (setq mac-command-modifier 'meta) ; sets the Command key as Meta
       (setq mac-option-modifier 'super) ; sets the left Option key as Super
       (setq mac-right-option-modifier 'hyper) ; sets the Option key as Hyper
       (setq mac-control-modifier 'ctrl) ; sets the Control key as Control
       ))

;; key bindings
(global-set-key (kbd "C-f") 'forward-word)  ; M-f forward-word
(global-set-key (kbd "C-b") 'backward-word)  ; M-b backward-word
(global-set-key (kbd "C-.") 'semantic-complete-analyze-inline) ; M-x semantic-complete-analyze-inline
(global-set-key (kbd "C-,") 'flymake-display-err-menu-for-current-line) ; M-x flymake-display-err-menu-for-current-line
(global-set-key (kbd "C-x e") 'erase-buffer) ; M-x erase-buffer (erase entire buffer)
(global-set-key (kbd "C-s-<left>") 'shrink-window-horizontally)    ; shrink window horizontally
(global-set-key (kbd "C-s-<right>") 'enlarge-window-horizontally) ; enlarge window horizontally
(global-set-key (kbd "C-s-<down>") 'shrink-window) ; shrink window vertically
(global-set-key (kbd "C-s-<up>") 'enlarge-window) ; enlarge window vertically

;; others
(setq make-backup-files nil)      ; no backup files (which end with ~)
(setq auto-save-default nil) ; no auto save files (which is surrounded by #)
(setq inhibit-splash-screen t) ; disable splash screen (no startup screen, enter *scratch*)
(setq show-help-function nil) ; disable the tooltips in modeline (Disable whole help function, better to use M-x tooltip-mode RET)
(setq ns-pop-up-frames nil) ; open in new buffer instead of new frame when choose "open with emacs"
(setq confirm-nonexistent-file-or-buffer nil)
(setq c-basic-indent 2)            ; set up tabs
(setq-default c-basic-offset 4
          tab-width 4
	  indent-tabs-mode nil)        ; set tab indent to 4 spaces in cc mode (C, java ...)
; (server-start)            ; start server (type `emacs` can open file in current instance of emacs. `emacs` is alias to `emacsclient`)
(defalias 'yes-or-no-p 'y-or-n-p) ; answer `y` or `n` for `yes` or `no
