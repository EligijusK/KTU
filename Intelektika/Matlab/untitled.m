  persistent INFO;
  if isempty(INFO), INFO = get_info; end
  if (nargin > 0) && ischar(varargin{1}) ...
      && ~strcmpi(varargin{1},'hardlim') && ~strcmpi(varargin{1},'hardlims')
    code = varargin{1};
    switch code
      case 'info',
        out1 = INFO;
      case 'check_param'
        err = check_param(varargin{2});
        if ~isempty(err), nnerr.throw('Args',err); end
        out1 = err;
      case 'create'
        if nargin < 2, error(message('nnet:Args:NotEnough')); end
        param = varargin{2};
        err = nntest.param(INFO.parameters,param);
        if ~isempty(err), nnerr.throw('Args',err); end
        out1 = create_network(param);
        out1.name = INFO.name;
      otherwise,
        % Quick info field access
        try
          out1 = eval(['INFO.' code]);
        catch %#ok<CTCH>
          nnerr.throw(['Unrecognized argument: ''' code ''''])
        end
    end
  else
    [args,param] = nnparam.extract_param(varargin,INFO.defaultParam);
    [param,err] = INFO.overrideStructure(param,args);
    if ~isempty(err), nnerr.throw('Args',err,'Parameters'); end
    net = create_network(param);
    net.name = INFO.name;
    out1 = init(net);
  end
end

function v = fcnversion
  v = 7;
end

%  BOILERPLATE_END
%% =======================================================

function info = get_info
  info = nnfcnNetwork(mfilename,'Linear Designed',fcnversion, ...
    [ ...
    nnetParamInfo('inputs','Input Data','nntype.data',{},...
    'Input data.'), ...
    nnetParamInfo('targets','Target Data','nntype.data',{},...
    'Target output data.'), ...
    nnetParamInfo('inputStates','Initial Input States','nntype.data',{},...
    'Initial input delay states.'), ...
    ]);
end

function err = check_param(param)

  % Inputs
  x = param.inputs;
  if ~iscell(x), x = {x}; end
  [Nx,Qx,TSx,Sx] = nnfast.nnsize(x);
  
  % Targets
  t = param.targets;
  if ~iscell(t), t = {t}; end
  [Nt,Qt,TSt,St] = nnfast.nnsize(t);
  if (Qx ~= Qt)
    err = 'Inputs and targets have different numbers of samples.';
    return
  elseif (TSx ~= TSt)
    err = 'Inputs and targets have different numbers of timesteps.';
    return
  end
  
  % Delayed Inputs
  xi = param.inputStates;
  [Nz,Qz,TSz,Sz] = nnfast.nnsize(xi);
  if ~isempty(xi)
    if ~iscell(xi), xi = {xi}; end
    if (Sz ~= Sx)
      err = 'Inputs and input states have different numbers of signals.';
      return
    end
    if any(Nz ~= Nx)
      err = 'Inputs and input states have different numbers of elements.';
      return
    elseif (Qz ~= Qx)
      err = 'Inputs and input states have different numbers of samples.';
      return
    end
  end

  err = '';
end

function net = create_network(param)

  
  % Inputs
  x = param.inputs;
  if ~iscell(x), x = {x}; end
  [Nx,Q,TS,Sx] = nnfast.nnsize(x);
  
  % Targets
  t = param.targets;
  if ~iscell(t), t = {t}; end
  [Nt,Qt,TSt,St] = nnfast.nnsize(t);
  
  % Input States
  xi = param.inputStates;
  if isempty(xi), cell(Sx,0); end
  if ~iscell(xi), xi = {xi}; end
  [Nz,Qz,TSz,Sz] = nnfast.nnsize(xi);
  
  % Combine signals
  X = cell(1,TS);
  T = cell(1,TS);
  for i=1:TS
    X{i} = cell2mat(x(:,i));
    T{i} = cell2mat(t(:,i));
  end
  Xi = cell(1,TSz);
  for i=1:TSz
    Xi{i} = cell2mat(xi(:,i));
  end
  
  % Delayed inputs
  Xc = [Xi X];
  Xd = cell(1,TS);
  delays = 0:TSz;
  for ts=1:TS
    Xd{ts} = tapdelay(Xc,1,TSz+ts,delays);
  end
  
  % Flatten time
  Xd = cell2mat(Xd);
  T = cell2mat(T);
  
  % Weights & Biases
  [lwm,lwid] = lastwarn;
  ws = warning('off','MATLAB:rankDeficientMatrix');
  Wb = T/[Xd; ones(1,Q*TS)];
  warning(ws);
  lastwarn(lwm,lwid);
  
  IW = Wb(:,1:sum(Nx)*length(delays));
  b = Wb(:,sum(Nx)*length(delays)+1);
  
  % Break up weights and biases
  IW = mat2cell(IW,Nt,Nx*length(delays));
  b = mat2cell(b,Nt,1);
  
  % Network architecture
  net = network(Sx,St,ones(St,1),ones(St,Sx),zeros(St,St),ones(1,St));

  for i=1:Sx
    net.inputs{i}.range = minmax([x{i,:}]);
  end
  for i=1:St
    net.outputs{i}.range = minmax([t{i,:}]);
  end
  for i=1:St
    net.b{i} = b{i};
    for j=1:Sx
      net.inputWeights{i,j}.delays = delays;
      net.IW{i,j} = IW{i,j};
    end
  end
end