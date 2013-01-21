install
nfs --server=192.168.0.4 --dir=/media/software/linux/distributions/centos/6.3/os/i386
lang en_US.UTF-8
keyboard us
network --onboot yes --device eth0 --mtu=1500 --bootproto dhcp --ipv6 auto --hostname test1
rootpw  --iscrypted $6$5LkqInxECgnHsOQC$YpNOLnnls29ngoFVzg2dq6Frnt79fnHk73hcB5RMH/tSuegP9lqKyjfkVz.wikYK5t5AHM.3z3y/Nu.i..Rct0
reboot
firewall --service=ssh
authconfig --enableshadow --passalgo=sha512
selinux --enforcing
timezone --utc America/Los_Angeles
bootloader --location=mbr --driveorder=vda --append="crashkernel=auto rhgb"
zerombr
autopart
logging --host=192.168.0.5

repo --name="CentOS"  --baseurl=nfs:192.168.0.4:/media/software/linux/distributions/centos/6.3/os/i386 --cost=100
repo --name="PeopleMerge"  --baseurl=nfs:192.168.0.4:/media/software/rpm/RPMS/i386 --cost=100
repo --name="puppetlabs"  --baseurl=http://yum.puppetlabs.com/el/6/products/i386 --cost=100
repo --name="puppetlabs-deps"  --baseurl=http://yum.puppetlabs.com/el/6/dependencies/i386 --cost=100

%packages --nobase
@core
zookeeper
libzookeeper
libzookeeper-devel
python-zookeeper
nfs-utils
openssh-clients
openssh
puppet
%post

mkdir /mnt/temp
echo mount 192.168.0.4:/media /mnt/temp >>/root/mount-out
mount -o nolock 192.168.0.4:/media /mnt/temp 2>> /root/mount-out
cp /mnt/temp/software/kickstart/createZkNodes.sh /root
umount /mnt/temp

cat >>/etc/hosts <<EOF
192.168.0.4  heracles
192.168.0.5  ino
EOF

# TODO this is for zookeeper, we could make this optional someday, replaced with something else.
cat >>/etc/rc.local <<EOF
/root/createZkNodes.sh
EOF

puppet resource ssh_authorized_key 'god@heaven' ensure=present type=ssh-rsa user=root key='AAAAB3NzaC1yc2EAAAABIwAAAQEAxJNexZQyrppEaec9s+sFA14MuD2cLmLK90kcVoMiC31cnbB/oGTdPACnBRluvwaI6D6gJ9kUlqf3qka9GJqFUY6k7TFiuCOPpMrxZV5Eyk+p+C2FtW+Q/qwMCZecnYnmyRzXaYe2IZ1uOrpPdbty0GHOleDFqHbpzWgyjMVpPTjOO21js/jm1dPxCn1q8FpYmb0DDqEUHBXQjlGsB4eHwDiRNWfARIXV0KIZqp2bvdRqy7yty+21kPLIL6wgNN/Q4nb/swFoNiTO0UivSmDPh62FAzfQWObONPqjLGEpuBkrPY1yrIlU+KEqsD11ZR0f5M6wTFGQi9goQss3z3bgfQ=='  &>>/root/puppet-out
puppet resource host puppetmaster1.peoplemerge.com ensure=present ip=192.168.0.7 host_aliases=puppetmaster1 &>>/root/puppet-out

cat >> /etc/puppet/puppet.conf <<EOF
    server = puppetmaster1.peoplemerge.com
EOF

puppet agent --test

%end
