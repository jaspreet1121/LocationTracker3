DeltaT = 0.1;
F = [1 DeltaT; 0 1];
G = [DeltaT^2/2; DeltaT];
H = [1 0];

x0 = [0;0];
sigma_a = 0.1;

Q = sigma_a^2;
R = 0.1;

N = 1000;

a = randn(1,N)*sigma_a;

x_truth(:,1) = x0;
for t=1:N
    x_truth(:,t+1) = F*x_truth(:,t) + G*a(t);
    y(t) = H*x_truth(:,t) + randn(1,1)*sqrt(R);
end

%Kalman Filter
p0 = 100*eye(2,2);

xx(:,1) = x0;
pp = p0;
pp_norm(1) = norm(pp);
for t=1:N
    [x1,p1,x,p] = kalman(y(t),xx(:,t),pp,F,G,H,Q,R);
    xx(:,t+1) = x1;
    pp = p1;
    pp_norm(t+1) = norm(pp);
end