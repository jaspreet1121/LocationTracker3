clear all
close all
clc
num=xlsread("LocationTracker1.xlsx");
a=num(:,2:4);

%load walking
%clear m
%N=length(t);
N=2500;
% The model parameters

% The initial values
Q=0.5*eye(9);  R=eye(3);
P=5*eye(9);  x=randn(9,1);
X=[];
KK=[];
a_m=[];
a_e=[];
for n=2:N
    %tme(n)=t(n);
    %h=tme(n)-tme(n-1);
    h=30;
    h2=h^2/2;
    Ph=[1  0  0  h  0  0  h2 0   0
		0  1  0  0  h  0  0  h2  0  
		0  0  1  0  0  h  0  0   h2
		0  0  0  1  0  0  h  0   0
		0  0  0  0  1  0  0  h   0
		0  0  0  0  0  1  0  0   h
		0  0  0  0  0  0  1  0   0
		0  0  0  0  0  0  0  1   0
		0  0  0  0  0  0  0  0   1  ];

    H=[zeros(3,6) eye(3)];    
    
    % Get the noisy measurement
    z=a(n,:)';
    a_m=[a_m;z'];
    % Compute the Kalman Gain
    K=P*H'*inv(H*P*H'+R);
    KK=[KK;K(:)'];

    % Update the estimate
    z_cap=H*x;
    a_e=[a_e;z_cap'];
    x=x+K*(z-z_cap);
    X=[X;x(:)'];
    
    %Compute the posterior error covariance
    P=(eye(9)-K*H)*P;
    
    % Project Ahead
    x=Ph*x;
    P=Ph*P*Ph'+Q;
   
%      plot([a_m a_e])

shg
end

figure; plot([a_m(:,1) a_e(:,1)])
figure; plot([a_m(:,2) a_e(:,2)])
figure; plot([a_m(:,3) a_e(:,3)])
