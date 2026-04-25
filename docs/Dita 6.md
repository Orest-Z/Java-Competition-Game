Dita 6 — VFX, Game Logic & Audio Fixes
Objektivi: Shtimi i efekteve vizuale për të rritur impaktin e lojës gjatë përplasjeve dhe pastrimi rrënjësor i bugs-ave të audios dhe logjikës.

1.Screen Shake: Implementimi i dridhjes së ekranit me anë të translate në Graphics2D për t'i dhënë më shumë peshë dhe reagim vizual momentit të përplasjes.

2.Spark Particles: Krijimi i klasës së brendshme Particle që gjeneron shkëndija (sparks) me drejtim, shpejtësi dhe jetëgjatësi random në koordinatat e makinës gjatë një gameOver.

3.Score Pulse Effect: Animacion dinamik ku teksti i SCORE zmadhohet dhe bëhet i artë për 20 frames sa herë që lojtari shton një monedhë në ekonomi (çdo 10 pikë).

4.Game Loop Restructure: Ndarja e qartë e metodës update() në dy blloqe — lëvizja/spawning ndalon menjëherë pas gameOver, ndërsa loop-i i efekteve vizuale (shake & particles) lejohet të mbarojë animacionin.

5.Audio Bugs Fixed: Futja e flag-ut crashPlayed në checkCollisions() për të parandaluar thirrjen e crashSFX.wav 20 herë rresht në të njëjtin frame, si dhe ruajtja e muzikës persistente mes paneleve.

6.Fullscreen Hitbox Fix: Zgjidhja e bug-ut kritik ku makina kalonte poshtë armiqve pa bërë collision pas aplikimit të Settings — u rregullua duke injektuar revalidate() dhe repaint() te dritarja e MainFrame.