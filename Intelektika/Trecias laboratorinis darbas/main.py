import numpy as np
import matplotlib.pyplot as plt


def trapecia(x, abcd):

    assert len(abcd) == 4, 'abcd parameter must have exactly four elements.'
    a, b, c, d = np.r_[abcd]
    assert a <= b and b <= c and c <= d, 'abcd requires the four elements \
                                          a <= b <= c <= d.'
    y = np.ones(len(x))

    idx = np.nonzero(x <= b)[0]
    y[idx] = triangle(x[idx], np.r_[a, b, b])

    idx = np.nonzero(x >= c)[0]
    y[idx] = triangle(x[idx], np.r_[c, c, d])

    idx = np.nonzero(x < a)[0]
    y[idx] = np.zeros(len(idx))

    idx = np.nonzero(x > d)[0]
    y[idx] = np.zeros(len(idx))

    return y


def triangle(x, abc):

    assert len(abc) == 3, 'abc parameter must have exactly three elements.'
    a, b, c = np.r_[abc]     # Zero-indexing in Python
    assert a <= b and b <= c, 'abc requires the three elements a <= b <= c.'

    y = np.zeros(len(x))

    # Left side
    if a != b:
        idx = np.nonzero(np.logical_and(a < x, x < b))[0]
        y[idx] = (x[idx] - a) / float(b - a)

    # Right side
    if b != c:
        idx = np.nonzero(np.logical_and(b < x, x < c))[0]
        y[idx] = (c - x[idx]) / float(c - b)

    idx = np.nonzero(x == b)
    y[idx] = 1
    return y


def pointPosTri(x, a, b, c):
    if(x < a):
        return 0
    elif(a <= x < b):
        return (x-a)/(b-a)
    elif(b <= x <= c):
        return (c-x)/(c-b)
    elif(x > c):
        return 0


def ProintPosTrap(x, a, b, c, d):
    if (x < a):
        return 0
    elif (a <= x < b):
        return (x - a) / (b - a)
    elif (b <= x < c):
        return 1
    elif(c <= x < d):
        return (d - x) / (d - c)
    elif(d <= x):
        return 0

def MOM(x, mfx):
    val = mfx == mfx.max()
    return np.mean(x[val])


def centroid(x, mfx):

    sum_moment_area = 0.0
    sum_area = 0.0

    # If the membership function is a singleton fuzzy set:
    if len(x) == 1:
        return x[0]*mfx[0] / np.fmax(mfx[0], np.finfo(float).eps).astype(float)

    # else return the sum of moment*area/sum of area
    # checking for triangles and square
    for i in range(1, len(x)):
        x1 = x[i - 1]
        x2 = x[i]
        y1 = mfx[i - 1]
        y2 = mfx[i]

        # if y1 == y2 == 0.0 or x1==x2: --> rectangle of zero height or width
        if not(y1 == y2 == 0.0 or x1 == x2):
            if y1 == y2:  # rectangle
                moment = 0.5 * (x1 + x2)
                area = (x2 - x1) * y1
            elif y1 == 0.0 and y2 != 0.0:  # triangle, height y2
                moment = 2.0 / 3.0 * (x2-x1) + x1
                area = 0.5 * (x2 - x1) * y2
            elif y2 == 0.0 and y1 != 0.0:  # triangle, height y1
                moment = 1.0 / 3.0 * (x2 - x1) + x1
                area = 0.5 * (x2 - x1) * y1
            else:
                moment = (2.0 / 3.0 * (x2-x1) * (y2 + 0.5*y1)) / (y1+y2) + x1
                area = 0.5 * (x2 - x1) * (y1 + y2)

            sum_moment_area += moment * area
            sum_area += area

    return sum_moment_area / np.fmax(sum_area, np.finfo(float).eps).astype(float)


x_dist = np.arange(0, 31, 1)
x_hour = np.arange(0, 24, 1)
x_stop = np.arange(1, 21, 1)
x_dur = np.arange(1, 66, 1)


dist_lo = trapecia(x_dist, [0, 0, 7, 12])
dist_md = trapecia(x_dist, [8, 11, 19, 24])
dist_hi = trapecia(x_dist, [20, 22, 30, 30])

hour_nig = trapecia(x_hour, [0, 0, 6, 8])
hour_mor = trapecia(x_hour, [6, 9, 10, 12])
hour_day = trapecia(x_hour, [10, 12, 17, 19])
hour_eve = trapecia(x_hour, [17, 20, 23, 23])
# hour_mor_pic = triangle(x_hour, [7, 8, 9])
# hour_eve_pic = triangle(x_hour, [16, 17, 18])

stop_lo = trapecia(x_stop, [1, 1, 4, 6])
stop_md = triangle(x_stop, [4, 10, 16])
stop_hi = trapecia(x_stop, [14, 16, 20, 20])

dur_lo = trapecia(x_dur, [1, 1, 17, 30])
dur_md = triangle(x_dur, [20, 35, 50])
dur_hi = trapecia(x_dur, [40, 55, 65, 65])

fig, ((ax0, ax1), (ax2, ax3)) = plt.subplots(nrows=2, ncols=2, figsize=(8, 9))

ax0.plot(x_dist, dist_lo, 'b', linewidth=1.5, label='Low')
ax0.plot(x_dist, dist_md, 'g', linewidth=1.5, label='Mid')
ax0.plot(x_dist, dist_hi, 'r', linewidth=1.5, label='High')
ax0.set_title('Distance')
ax0.legend()

ax1.plot(x_hour, hour_nig, 'b', linewidth=1.5, label='Night')
ax1.plot(x_hour, hour_mor, 'y', linewidth=1.5, label='Morning')
ax1.plot(x_hour, hour_day, 'c', linewidth=1.5, label='Day')
ax1.plot(x_hour, hour_eve, 'k', linewidth=1.5, label='Evening')
# ax1.plot(x_hour, hour_mor_pic, 'g', linewidth=1.5, label='v')
# ax1.plot(x_hour, hour_eve_pic, 'r', linewidth=1.5, label='vz')
ax1.set_title('Time of day')
ax1.legend()

ax2.plot(x_stop, stop_lo, 'b', linewidth=1.5, label='Low')
ax2.plot(x_stop, stop_md, 'g', linewidth=1.5, label='Mid')
ax2.plot(x_stop, stop_hi, 'r', linewidth=1.5, label='High')
ax2.set_title('Number of stops')
ax2.legend()

ax3.plot(x_dur, dur_lo, 'b', linewidth=1.5, label='Low')
ax3.plot(x_dur, dur_md, 'g', linewidth=1.5, label='Mid')
ax3.plot(x_dur, dur_hi, 'r', linewidth=1.5, label='High')
ax3.set_title('Traveling duration')
ax3.legend()

plt.show()


dist_level_lo = ProintPosTrap(5, 0, 0, 7, 12)
dist_level_md = ProintPosTrap(5, 8, 11, 19, 24)
dist_level_hi = ProintPosTrap(5, 20, 22, 30, 30)

hour_level_nig = ProintPosTrap(6.5, 0, 0, 6, 8)
hour_level_mor = ProintPosTrap(6.5, 6, 9, 10, 12)
hour_level_day = ProintPosTrap(6.5, 10, 12, 17, 19)
hour_level_eve = ProintPosTrap(6.5, 17, 20, 23, 23)

stop_level_lo = ProintPosTrap(13, 1, 1, 4, 6)
stop_level_md = pointPosTri(13, 4, 10, 16)
stop_level_hi = ProintPosTrap(13, 14, 16, 20, 20)

active_rule1 = np.fmax(dist_level_hi, stop_level_hi)
active_rule2 = np.fmin(dist_level_lo, hour_level_nig)
active_rule3 = np.fmin(dist_level_md, stop_level_hi)
active_rule4 = np.fmin(stop_level_lo, dist_level_lo)
active_rule5 = np.fmax(dist_level_md, stop_level_md)
active_rule6 = np.fmax(hour_level_mor, stop_level_hi)
active_rule7 = np.fmin(dist_level_hi, hour_level_day)
active_rule8 = np.fmin(dist_level_md, stop_level_hi)
active_rule9 = np.fmax(stop_level_lo, hour_level_nig)
active_rule10 = np.fmin(dist_level_md, stop_level_lo)
active_rule11 = np.fmin(stop_level_md, dist_level_hi)
active_rule12 = np.fmin(hour_level_day, dist_level_lo)
active_rule13 = np.fmin(dist_level_lo, stop_level_md)

rule_low = max([active_rule2, active_rule4, active_rule9, active_rule12])

rule_md = max([active_rule5, active_rule7, active_rule10, active_rule13])

rule_hi = max([active_rule1, active_rule3, active_rule6, active_rule8, active_rule11])

insurance_activation_lo = np.fmin(rule_low, dur_lo)


insurance_activation_md = np.fmin(rule_md, dur_md)

insurance_activation_hi = np.fmin(rule_hi, dur_hi)

insuranceZeros = np.zeros_like(x_dur)

fig, ax0 = plt.subplots(figsize=(8, 3))

ax0.fill_between(x_dur, insuranceZeros, insurance_activation_lo, facecolor='b', alpha=0.7)
ax0.plot(x_dur, dur_lo, 'b', linewidth=0.5, linestyle='--', )
ax0.fill_between(x_dur, insuranceZeros, insurance_activation_md, facecolor='b', alpha=0.7)
ax0.plot(x_dur, dur_md, 'y', linewidth=0.5, linestyle='--')
ax0.fill_between(x_dur, insuranceZeros, insurance_activation_hi, facecolor='b', alpha=0.7)
ax0.plot(x_dur, dur_hi, 'g', linewidth=0.5, linestyle='--')

plt.tight_layout()
plt.show()

aggregated = np.fmax(insurance_activation_lo, np.fmax(insurance_activation_md, insurance_activation_hi)) # taisykliu sarasas

defuzz_centroid = centroid(x_dur, aggregated)
defuzz_mom = MOM(x_dur, aggregated)

print("Centroid metodas - siūloma alga programuotojui: " + str(defuzz_centroid))
print("Bisector metodas - siūloma alga programuotojui: " + str(defuzz_mom))
