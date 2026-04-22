Dita 3 — Enemy Cars & Collision Detection
Objektivi: Shtimi i pengesave dhe logjikës së përfundimit të lojës.

1.Klasa EnemyCar: Objekt i pavarur me x, y, speed, hitbox dhe bodyColor — ndjek parimin OOP ku çdo objekt menaxhon vetë gjendjen dhe vizatimin e tij

2.Spawning System: spawnTimer numëron frame-et dhe çdo 90 frame krijon një makinë të re në një korsi random (Math.random() * 4)

3.Pozicionimi në korsi: Formula ROAD_LEFT + (lane * laneWidth) + (laneWidth/2) - (CAR_WIDTH/2) centron saktë makinën brenda korsisë

4.Lëvizja & Pastrimi: Iterator i sigurt heq makinat që kalojnë PANEL_HEIGHT pa shkaktuar ConcurrentModificationException

5.Ngjyra Random: Array me 5 ngjyra të ndryshme — çdo makinë armike merr një ngjyrë random nga lista në momentin e spawnimit

6.Vizatim Detajor: Metoda draw() në EnemyCar pikturon gomërat, trupin, çatinë, xhamat dhe fenerët — identike me makinën e lojtarit

7.Collision Detection: Rectangle.intersects() me inset 5px krijon hitbox më të vogël se vizuali për collision të ndjerë si të drejtë

8.Game Over & Restart: Flag gameOver ndalon loop-in, shfaq mesazhin dhe butonn RESTART që pastron listën dhe rinis lojën