Dita 6 — Polish Vizual & Sisteme të Avancuara
Objektivi: Eliminimi i lodhjes vizuale dhe shtimi i kontrollit mbi lojën.

1.Road Gradient: GradientPaint në drawRoad() — rruga kalon nga e errët majtas në më të ndritshme djathtas; vijat ndarëse me alpha 100 për të shmangur efektin "strobe"

2.Parallax Scenery: Pemë me detaje (trungu, kurora, highlight, hija) majtas dhe gurë me shtresa djathtas — lëvizin me shpejtësi të ndryshme (+3 vs +2) për ndjesi thellësie

3.Pause System: ESC ndalon gameTimer dhe thirr repaint() — overlay gjysmë-transparent me tekst "PAUSED" dhe udhëzim për vazhdim

4.Level-Up Notification: Tekst "LEVEL UP!" me fade-out (alpha = levelUpTimer / 30f) që shfaqet 1.5 sekonda sa herë rritet vështirësia

5.Smart Spawning: Shuffle i korsive {0,1,2,3} — mbi 500 pikë: 30% mundësi për 2 makina; mbi 1500: 20% shtesë për 3 makina në korsi të ndryshme

6.Game Over Box: Panel i unifikuar me border të kuq që përfshin GAME OVER, monedhat e fituara, rekord i ri dhe butonat RESTART/MENU

7.Score Dinamik: Ngjyrë e kuqe kur po thyhet rekordi, ari kur pulse, e bardhë normale