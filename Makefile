all: submodule dev
	jar cvfe SpaceInvaders.jar SpaceInvaders *
	$(MAKE) clean-leavejar

dev: submodule
	javac *.java

run:
	java SpaceInvaders

submodule: clean
	git submodule update --init --recursive --remote --merge
	git submodule foreach git pull origin master
	cp -r unsigned/littlecube littlecube

clean: clean-leavejar
	rm *.jar || continue

clean-leavejar: cleansub
	rm *.class || continue
	rm -r littlecube || continue

cleansub:
	git submodule deinit --all --force