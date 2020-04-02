load sunspot.txt
L = length(sunspot); 	% duomen? kiekis
P = [sunspot(1:L-2,2)' ; 	% ?vesties duomen?
     sunspot(2:L-1,2)']; 	% matrica
T = sunspot(3:L,2)'; 	% išvesties duomen?
figure(1)
plot3(P(1,:),P(2,:),T,'bo')
Pu = P(:,1:200);
Tu = T(1:200);
net = newlind(Pu, Tu);
w1 = net.IW{1}(1)
w2 = net.IW{1}(2)
b = net.b{1};
Tsu = sim(net,Pu);

